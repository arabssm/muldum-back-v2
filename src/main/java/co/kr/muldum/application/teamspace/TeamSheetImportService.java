package co.kr.muldum.application.teamspace;

import co.kr.muldum.domain.teamspace.model.Member;

import co.kr.muldum.domain.user.model.Student;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.MemberRepository;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.user.repository.StudentRepository;
import co.kr.muldum.infrastructure.sheets.GoogleSheetsClient;
import co.kr.muldum.presentation.dto.TeamSheetImportRequestDto;
import co.kr.muldum.presentation.dto.TeamSheetImportResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamSheetImportService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;
    private final GoogleSheetsClient googleSheetsClient;

    public TeamSheetImportResponseDto importFromSheet(TeamSheetImportRequestDto teamSheetImportRequestDto) {
        List<TeamSheetImportResponseDto.ErrorDetail> errors = new ArrayList<>();
        // 1. 구글 시트 링크에서 sheetId 추출
        String link = teamSheetImportRequestDto.getSheetLink();
        String sheetId = extractSheetId(link);

        // 2. GoogleSheetsClient로 시트 데이터 읽기
        // 시트 이름은 요청에 포함되거나 기본값 사용
        String range = teamSheetImportRequestDto.getRange();
        java.util.List<java.util.List<Object>> rows = googleSheetsClient.readRows(sheetId, (range != null ? range : "A2:D"));
        int totalRows = (rows != null) ? rows.size() : 0;

        int skipped = 0, failed = 0, teamsUpserted = 0, studentsUpserted = 0;
        int membersUpserted = 0;

        if (rows != null) {
            int rowIndex = 0;
            for (java.util.List<Object> row : rows) {
                String teamName = row.size() > 0 ? (row.get(0) != null ? row.get(0).toString() : "") : "";
                String name = row.size() > 1 ? (row.get(1) != null ? row.get(1).toString() : "") : "";
                String email = row.size() > 2 ? (row.get(2) != null ? row.get(2).toString() : "") : "";
                String role = row.size() > 3 ? (row.get(3) != null ? row.get(3).toString() : "") : "";
                System.out.println("teamName: " + teamName + ", name: " + name + ", email: " + email + ", role: " + role);

                // Validation logic
                if (teamName.isBlank() && name.isBlank() && email.isBlank() && role.isBlank()) {
                    errors.add(new TeamSheetImportResponseDto.ErrorDetail(rowIndex, "Row is blank"));
                    skipped++;
                    rowIndex++;
                    continue;
                }
                if (teamName.isBlank()) {
                    errors.add(new TeamSheetImportResponseDto.ErrorDetail(rowIndex, "Team name is blank"));
                    failed++;
                    rowIndex++;
                    continue;
                }
                if (!isValidEmail(email)) {
                    errors.add(new TeamSheetImportResponseDto.ErrorDetail(rowIndex, "Invalid email format"));
                    failed++;
                    rowIndex++;
                    continue;
                }
                // Upsert Team by name
                if (!teamRepository.findByName(teamName).isPresent()) {
                    Team team = new Team();
                    team.setName(teamName);
                    team.setType("DEFAULT");
                    LocalDateTime now = LocalDateTime.now();
                    team.setCreatedAt(now);
                    team.setUpdatedAt(now);
                    teamRepository.save(team);
                    teamsUpserted++;
                }
                // Upsert Student by email
                java.util.Optional<Student> existingStudentOpt = studentRepository.findByEmail(email);
                if (existingStudentOpt.isPresent()) {
                    Student existingStudent = existingStudentOpt.get();
                    Object profileName = null;
                    if (existingStudent.getProfile() != null) {
                        profileName = existingStudent.getProfile().get("name");
                    }
                    if ((profileName == null || profileName.toString().isBlank()) && !name.isBlank()) {
                        existingStudent.setName(name);
                        studentRepository.save(existingStudent);
                    }
                } else {
                    Student newStudent = Student.create(email, name);
                    studentRepository.save(newStudent);
                    studentsUpserted++;
                }

                // Upsert Member by teamId and studentId
                Long teamId = teamRepository.findByName(teamName).get().getId();
                Long studentId = studentRepository.findByEmail(email).get().getId();
                if (!memberRepository.existsByTeamIdAndStudentId(teamId, studentId)) {
                    Member member = new Member();
                    member.setTeamId(teamId);
                    member.setStudentId(studentId);
                    member.setRole(role.isBlank() ? "MEMBER" : role.toUpperCase());
                    member.setDisplayName(name);
                    member.setCreatedAt(LocalDateTime.now());
                    member.setUpdatedAt(LocalDateTime.now());
                    memberRepository.save(member);
                    membersUpserted++;
                }
                rowIndex++;
            }
        }

        // 3. TODO: 데이터 파싱 및 DB 저장 로직 (추후 구현)

        return TeamSheetImportResponseDto.builder()
                .total(totalRows)
                .teamsUpserted(teamsUpserted)
                .membersUpserted(membersUpserted)
                .studentsUpserted(studentsUpserted)
                .skipped(skipped)
                .failed(failed)
                .errors(errors)
                .build();
    }

    // 구글 시트 링크에서 sheetId 추출
    private String extractSheetId(String link) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("/d/([^/]+)/");
        java.util.regex.Matcher matcher = pattern.matcher(link);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid Google Sheets link: " + link);
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}
