package co.kr.muldum.application.user;

import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.model.UserType;
import co.kr.muldum.domain.user.repository.UserRepository;
import co.kr.muldum.infrastructure.teamspace.GoogleSheetApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TeacherImportService {
    private final UserRepository userRepository;
    private final GoogleSheetApiClient googleSheetApiClient;

    @Transactional
    public List<User> importFromGoogleSheet(String googleSheetUrl) {
        try {
            String spreadsheetId = extractSpreadsheetId(googleSheetUrl);
            List<String> sheetNames = googleSheetApiClient.getSheetNames(spreadsheetId);
            if (sheetNames.isEmpty()) {
                throw new IllegalArgumentException("스프레드시트에 시트가 없습니다.");
            }

            String firstSheetName = sheetNames.getFirst();
            String range = String.format("%s!A1:ZZ", firstSheetName);
            List<List<Object>> rows = googleSheetApiClient.readSheet(spreadsheetId, range);
            if (rows.isEmpty()) {
                return List.of();
            }

            return processRows(rows);
        } catch (Exception e) {
            throw new RuntimeException("구글 시트 가져오기 실패", e);
        }
    }

    @Transactional
    public List<User> importFromCsv(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<List<Object>> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(new ArrayList<>(Arrays.asList(line.split(","))));
            }
            if (rows.isEmpty()) {
                return List.of();
            }

            List<Object> firstRow = rows.getFirst();
            boolean hasHeader = firstRow.size() >= 2 &&
                    firstRow.get(0).toString().trim().equalsIgnoreCase("name") &&
                    firstRow.get(1).toString().trim().equalsIgnoreCase("email");

            if (hasHeader) {
                return processRows(rows);
            } else {
                return processCsvRowsWithoutHeader(rows);
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV 파일 처리 실패", e);
        }
    }

    private List<User> processCsvRowsWithoutHeader(List<List<Object>> rows) {
        List<User> usersToSave = new ArrayList<>();

        for (List<Object> row : rows) {
            if (row == null || row.size() < 2) continue;

            String name = row.get(0).toString().trim();
            String email = row.get(1).toString().trim();

            if (userRepository.findByEmail(email).isPresent()) {
                continue;
            }

            User user = User.builder()
                    .email(email)
                    .name(name)
                    .userType(UserType.TEACHER)
                    .build();

            usersToSave.add(user);
        }

        return userRepository.saveAll(usersToSave);
    }

    private List<User> processRows(List<List<Object>> rows) {
        List<Object> headerRow = rows.getFirst();
        List<User> usersToSave = new ArrayList<>();

        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            if (row == null || row.isEmpty()) continue;

            Map<String, Object> dataMap = new HashMap<>();
            for (int col = 0; col < headerRow.size() && col < row.size(); col++) {
                String key = headerRow.get(col).toString().trim();
                Object value = row.get(col);
                dataMap.put(key, value);
            }

            String email = Optional.ofNullable(dataMap.get("email"))
                    .map(Object::toString)
                    .orElseThrow(() -> new IllegalArgumentException("email 컬럼이 비어있습니다."));

            if (userRepository.findByEmail(email).isPresent()) {
                continue;
            }

            Map<String, Object> profile = new HashMap<>(dataMap);
            profile.remove("email");

            String name = Optional.ofNullable(profile.remove("name"))
                    .map(Object::toString)
                    .map(String::trim)
                    .orElse(null);

            User user = User.builder()
                    .email(email)
                    .name(name)
                    .profile(profile)
                    .userType(UserType.TEACHER)
                    .build();

            usersToSave.add(user);
        }

        return userRepository.saveAll(usersToSave);
    }

    private String extractSpreadsheetId(String url) {
        Pattern pattern = Pattern.compile("/spreadsheets/d/([a-zA-Z0-9-_]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid Google Sheet URL");
    }
}
