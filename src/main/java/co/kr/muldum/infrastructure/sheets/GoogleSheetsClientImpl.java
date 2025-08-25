package co.kr.muldum.infrastructure.sheets;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Collections;

@Component
public class GoogleSheetsClientImpl implements GoogleSheetsClient {

    @Override
    public List<List<Object>> readRows(String sheetId, String range) {
        // TODO: Implement actual Google Sheets API integration
        return Collections.emptyList();
    }
}
