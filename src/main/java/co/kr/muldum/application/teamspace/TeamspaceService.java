package co.kr.muldum.application.teamspace;

import co.kr.muldum.domain.user.model.Student;
import co.kr.muldum.domain.user.repository.StudentRepository;
import co.kr.muldum.infrastructure.teamspace.GoogleSheetApiClient;
import co.kr.muldum.presentation.teamspace.dto.TeamspaceInviteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TeamspaceService {

    private final StudentRepository studentRepository;
    private final GoogleSheetApiClient googleSheetApiClient;

    public TeamspaceInviteResponseDto inviteStudents(StudentCsvImportRequest studentCsvImportRequest) {
        try {
            Objects.requireNonNull(studentCsvImportRequest, "request must not be null");
            String url = studentCsvImportRequest.getGoogleSheetUrl();
            if (url == null || url.isBlank()) {
                throw new IllegalArgumentException("googleSheetUrl must not be blank");
            }
            // 스프레드시트 ID 추출
            String spreadsheetId = extractSpreadsheetId(url);
            // 시트 이름 가져오기
            List<String> sheetNames = googleSheetApiClient.getSheetNames(spreadsheetId);
            if (sheetNames == null || sheetNames.isEmpty()) {
                throw new IllegalArgumentException("스프레드시트에 시트가 없습니다.");
            }
            // 시트 데이터 읽기 (첫 번째 시트)
            String firstSheetName = sheetNames.get(0);
            String range = firstSheetName + "!A1:ZZ";
            List<List<Object>> sheetData = googleSheetApiClient.readSheet(spreadsheetId, range);
            if (sheetData == null || sheetData.size() < 2) {
                throw new RuntimeException("Sheet data is empty or missing header/data rows");
            }
            // 헤더와 데이터 매핑
            List<Object> headers = sheetData.get(0);
            for (int i = 1; i < sheetData.size(); i++) {
                List<Object> row = sheetData.get(i);
                Map<String, Object> mapped = new HashMap<>();
                for (int j = 0; j < headers.size() && j < row.size(); j++) {
                    String key = String.valueOf(headers.get(j)).trim().toLowerCase(Locale.ROOT);
                    mapped.put(key, row.get(j));
                }
                // 이메일 추출 및 학생 저장
                String email = Objects.toString(mapped.get("email"), null);
                if (email == null || email.isEmpty()) continue;
                email = email.trim().toLowerCase(Locale.ROOT);
                if (!email.endsWith("@bssm.hs.kr")) {
                    throw new IllegalArgumentException("허용되지 않은 도메인입니다.");
                }
                Optional<Student> optionalStudent = studentRepository.findByEmail(email);
                if (optionalStudent.isEmpty()) {
                    throw new IllegalArgumentException("등록되지 않은 사용자입니다.");
                }
                Student student = optionalStudent.get();
                studentRepository.save(student);
            }
            // 성공 응답 반환
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
