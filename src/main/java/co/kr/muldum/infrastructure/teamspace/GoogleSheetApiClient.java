package co.kr.muldum.infrastructure.teamspace;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoogleSheetApiClient {
  @Value("${google.sheets.key-file-path}")
  private String keyFilePath;

  private final Sheets sheetsService;

  public GoogleSheetApiClient(Sheets sheetsService) {
    this.sheetsService = sheetsService;
  }

  public List<String> getSheetNames(String spreadsheetId) {
    try {
      Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId)
              .setFields("sheets.properties.title")
              .execute();

      return spreadsheet.getSheets().stream()
              .map(sheet -> sheet.getProperties().getTitle())
              .collect(Collectors.toList());

    } catch (IOException e) {
      throw new RuntimeException("Failed to get sheet names from Google Sheets", e);
    }
  }

  public List<List<Object>> readSheet(String spreadsheetId, String range) throws Exception {
    try (FileInputStream fileInputStream = new FileInputStream(keyFilePath)) {
      ServiceAccountCredentials credentials = (ServiceAccountCredentials) ServiceAccountCredentials
              .fromStream(fileInputStream)
              .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

      Sheets service = new Sheets.Builder(
              GoogleNetHttpTransport.newTrustedTransport(),
              new JacksonFactory(),
              new HttpCredentialsAdapter(credentials))
              .setApplicationName("Student Importer")
              .build();

      ValueRange response = service.spreadsheets().values()
              .get(spreadsheetId, range)
              .execute();

      return response.getValues();
    }
  }
}
