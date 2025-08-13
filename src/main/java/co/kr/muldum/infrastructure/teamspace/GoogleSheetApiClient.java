package co.kr.muldum.infrastructure.teamspace;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoogleSheetApiClient {

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

  public List<List<Object>> readSheet(String spreadsheetId, String range) {
    try {
      ValueRange response = sheetsService.spreadsheets().values()
              .get(spreadsheetId, range)
              .execute();
      return response.getValues();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read sheet data", e);
    }
  }
}
