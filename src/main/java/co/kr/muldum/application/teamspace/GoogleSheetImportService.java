package co.kr.muldum.application.teamspace;

import co.kr.muldum.domain.user.repository.StudentRepository;
import co.kr.muldum.infrastructure.teamspace.GoogleSheetApiClient;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class GoogleSheetImportService {
  private final StudentRepository studentRepository;
  private final GoogleSheetApiClient googleSheetApiClient;

  @Transactional
  public List<String> importFromGoogleSheet(String googleSheetUrl) {
    try {
      String spreadsheetId = extractSpreadsheetId(googleSheetUrl);

      List<String> sheetNames = googleSheetApiClient.getSheetNames(spreadsheetId);

      if (sheetNames.isEmpty()) {
        throw new IllegalArgumentException("스프레드시트에 시트가 없습니다.");
      }

      // 2) 첫 번째 시트 이름으로 데이터 읽기
      String firstSheetName = sheetNames.getFirst();
      String range = String.format("%s!A1:ZZ", firstSheetName);
      List<List<Object>> rows = googleSheetApiClient.readSheet(spreadsheetId, range);
      if (rows.isEmpty()) {
        return List.of();
      }

      // 첫 행이 헤더: 컬럼명 리스트
      List<Object> headerRow = rows.getFirst();
      // headerRow 예: ["grade", "class", "studentId", "name", "email", ...]

      List<String> emails = new ArrayList<>();

      // 두 번째 행부터 데이터
      for (int i = 1; i < rows.size(); i++) {
        List<Object> row = rows.get(i);
        if (row == null || row.isEmpty()) continue;

        // Map<컬럼명, 값> 생성 (헤더 기준)
        Map<String, Object> dataMap = new HashMap<>();
        for (int col = 0; col < headerRow.size() && col < row.size(); col++) {
          String key = headerRow.get(col).toString();
          Object value = row.get(col);
          dataMap.put(key, value);
        }

        // 필수값 추출 및 검증 (email)
        String email = Optional.ofNullable(dataMap.get("email"))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("email 컬럼이 비어있습니다."));

        emails.add(email);
      }

      return emails;
    } catch (Exception e) {
      throw new RuntimeException("구글 시트 가져오기 실패", e);
    }
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
