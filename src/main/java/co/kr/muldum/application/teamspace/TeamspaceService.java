package co.kr.muldum.application.teamspace;

import co.kr.muldum.domain.user.model.Student;
import co.kr.muldum.domain.user.repository.StudentRepository;
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

    @Transactional
    public TeamspaceInviteResponseDto inviteStudents(StudentCsvImportRequest studentCsvImportRequest) {
        try {
            Objects.requireNonNull(studentCsvImportRequest, "request must not be null");
            String url = studentCsvImportRequest.getGoogleSheetUrl();
            if (url == null || url.isBlank()) {
                throw new IllegalArgumentException("googleSheetUrl must not be blank");
            }
            List<String> emails = googleSheetImportService.importFromGoogleSheet(url);
            for (String email : emails) {
                if (email == null || email.isEmpty()) continue;
                email = email.trim().toLowerCase(Locale.ROOT);
                if (!email.endsWith("@bssm.hs.kr")) {
                    throw new IllegalArgumentException("허용되지 않은 도메인입니다.");
                }
                Optional<Student> optionalStudent = studentRepository.findByEmail(email);
                if (optionalStudent.isEmpty()) {
                    throw new IllegalArgumentException("등록되지 않은 사용자입니다.");
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
