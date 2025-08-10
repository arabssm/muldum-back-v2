package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.CsvImportService;
import co.kr.muldum.application.teamspace.StudentCsvImportRequest;
import co.kr.muldum.application.teamspace.StudentCsvImportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tch/teamspace/invite")
@RequiredArgsConstructor
public class StudentCsvImportController {
  private final CsvImportService csvImportService;

  @PostMapping
  public ResponseEntity<StudentCsvImportResponse> importStudentsFromCsv(
          @RequestBody StudentCsvImportRequest studentCsvImportRequest
  ) {
    csvImportService.importFromGoogleSheet(studentCsvImportRequest.getGoogleSheetUrl());
    return ResponseEntity.ok(new StudentCsvImportResponse());
  }
}
