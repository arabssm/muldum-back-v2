package co.kr.muldum.infrastructure.sheets;

import java.util.List;

public interface GoogleSheetsClient {
    List<List<Object>> readRows(String sheetId, String range);
}
