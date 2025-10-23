package co.kr.muldum.application.teamspace;

import co.kr.muldum.domain.item.dto.ItemExcelResponseDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public ByteArrayInputStream createXlsx(List<ItemExcelResponseDto> items) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Approved Items");

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"물품ID", "물품명", "가격", "양", "링크", "신청자"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Body
        int rowNum = 1;
        for (ItemExcelResponseDto item : items) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getItemId());
            row.createCell(1).setCellValue(item.getProductName());
            row.createCell(2).setCellValue(item.getPrice());
            if (item.getQuantity() != null) {
                row.createCell(3).setCellValue(item.getQuantity());
            }
            row.createCell(4).setCellValue(item.getProductLink());
            row.createCell(5).setCellValue(item.getRequesterName());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}