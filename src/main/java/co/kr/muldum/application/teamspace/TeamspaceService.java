package co.kr.muldum.application.teamspace;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;

import co.kr.muldum.domain.user.model.Student;
import co.kr.muldum.domain.user.repository.StudentRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.teamspace.GoogleSheetApiClient;
import co.kr.muldum.presentation.teamspace.dto.TeamspaceInviteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TeamspaceService {

    private final StudentRepository studentRepository;
    private final GoogleSheetApiClient googleSheetApiClient;
    private final GoogleSheetImportService googleSheetImportService;
    private final TeamRepository teamRepository;
    private final TeamspaceMemberRepository teamspaceMemberRepository;

    @Transactional
    public TeamspaceInviteResponseDto inviteStudents(StudentCsvImportRequest studentCsvImportRequest) {
        try {
            Objects.requireNonNull(studentCsvImportRequest, "request must not be null");
            String url = studentCsvImportRequest.getGoogleSheetUrl();
            if (url == null || url.isBlank()) {
                throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
            }
            List<String> emails = googleSheetImportService.importFromGoogleSheet(url);
            // Placeholder: get the team instance (assume teamId = 1L for now)
            Team team = teamRepository.findById(1L)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_TEAM));
            for (String email : emails) {
                if (email == null || email.isEmpty()) continue;
                email = email.trim().toLowerCase(Locale.ROOT);
                if (!email.endsWith("@bssm.hs.kr")) {
                    throw new CustomException(ErrorCode.UNAUTHORIZED_DOMAIN);
                }
                Optional<Student> optionalStudent = studentRepository.findByEmail(email);
                if (optionalStudent.isEmpty()) {
                    throw new CustomException(ErrorCode.UNREGISTERED_USER);
                }
                Student student = optionalStudent.get();
                boolean alreadyMember = teamspaceMemberRepository.existsByTeamAndStudent(team, student);
                if (!alreadyMember) {
                    TeamspaceMember member = new TeamspaceMember(team, student, TeamspaceMember.Role.MEMBER);
                    teamspaceMemberRepository.save(member);
                }
            }
            return new TeamspaceInviteResponseDto("success");
        } catch (Exception e) {
            throw new RuntimeException("Failed to invite students: " + e.getMessage(), e);
        }
    }

    private String extractSpreadsheetId(String url) {
        Pattern pattern = Pattern.compile("/spreadsheets/d/([a-zA-Z0-9-_]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid Google Sheet URL: " + url);
    }
}
