package co.kr.muldum.application.teamspace;

import java.util.List;
import java.util.Collections;
import java.util.Map;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
@RequiredArgsConstructor
public class TeamSheetImportService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;
    private final GoogleSheetsClient googleSheetsClient;

    public TeamSheetImportResponseDto importFromSheet(TeamSheetImportRequestDto teamSheetImportRequestDto) {
        List<TeamSheetImportResponseDto.ErrorDetail> errors = new ArrayList<>();
        // 시트 ID 추출
        String link = teamSheetImportRequestDto.getSheetLink();
        String sheetId = extractSheetId(link);

        String range = teamSheetImportRequestDto.getRange();
        List<List<Object>> rows = googleSheetsClient.readRows(sheetId, (range != null ? range : "A2:D"));
        int totalRows = (rows != null) ? rows.size() : 0;

        int skipped = 0, failed = 0;
        AtomicInteger teamsUpserted = new AtomicInteger();
        AtomicInteger studentsUpserted = new AtomicInteger();
        int membersUpserted = 0;

        if (rows != null) {
            int rowIndex = 0;
            for (List<Object> row : rows) {
                String teamName = row.size() > 0 ? (row.get(0) != null ? row.get(0).toString() : "") : "";
                String name = row.size() > 1 ? (row.get(1) != null ? row.get(1).toString() : "") : "";
                String email = row.size() > 2 ? (row.get(2) != null ? row.get(2).toString() : "") : "";
                String role = row.size() > 3 ? (row.get(3) != null ? row.get(3).toString() : "") : "";
                System.out.println("teamName: " + teamName + ", name: " + name + ", email: " + email + ", role: " + role);

                // 입력값 검증
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
                Team team = teamRepository.findByName(teamName)
                    .orElseGet(() -> {
                        Team newTeam = Team.builder()
                            .name(teamName)
                            .type("DEFAULT")
                            .build();
                        teamRepository.save(newTeam);
                        teamsUpserted.incrementAndGet();
                        return newTeam;
                    });
                Student existingStudentCheck = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("학생을 찾을 수 없습니다."));
                Student student = studentRepository.findByEmail(email)
                    .map(existingStudent -> {
                        existingStudent.updateNameIfEmpty(name);
                        return existingStudent;
                    })
                    .orElseGet(() -> {
                        Student newStudent = Student.builder()
                            .email(email)
                            .profile(name.isBlank() ? Collections.emptyMap() : Map.of("name", name))
                            .build();
                        studentRepository.save(newStudent);
                        studentsUpserted.incrementAndGet();
                        return newStudent;
                    });

                Long teamId = team.getId();
                Long studentId = student.getId();
                if (!memberRepository.existsByTeamIdAndStudentId(teamId, studentId)) {
                    String normalizedRole = (role == null) ? "" : role.trim().toUpperCase();
                    normalizedRole = normalizedRole.isEmpty() ||
                                   (!normalizedRole.equals("LEADER") && !normalizedRole.equals("MEMBER"))
                                   ? "MEMBER" : normalizedRole;
                    LocalDateTime now = LocalDateTime.now();
                    Member member = Member.builder()
                        .teamId(teamId)
                        .studentId(studentId)
                        .role(normalizedRole)
                        .displayName(name)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                    try {
                        memberRepository.save(member);
                        membersUpserted++;
                    } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                    }
                }
                rowIndex++;
            }
        }

        // 결과 반환
        return TeamSheetImportResponseDto.builder()
                .total(totalRows)
                .teamsUpserted(teamsUpserted.get())
                .membersUpserted(membersUpserted)
                .studentsUpserted(studentsUpserted.get())
                .skipped(skipped)
                .failed(failed)
                .errors(errors)
                .build();
    }

    private String extractSheetId(String link) {
        if (link == null || link.isBlank()) {
            throw new IllegalArgumentException("Google Sheets link is null or blank");
        }
        Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9-_]+)");
        Matcher matcher = pattern.matcher(link);
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
