package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.GoogleSheetImportService;
import co.kr.muldum.application.teamspace.StudentCsvImportRequest;
import co.kr.muldum.application.teamspace.StudentCsvImportResponse;
import co.kr.muldum.domain.user.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tch/teamspace/invite")
@RequiredArgsConstructor
public class GoogleSheetImportController {
  private final GoogleSheetImportService googleSheetImportService;

  @PostMapping
  public ResponseEntity<StudentCsvImportResponse> importStudentsFromCsv(
          @RequestBody StudentCsvImportRequest studentCsvImportRequest
  ) {

    List<Student> savedStudents = googleSheetImportService.importFromGoogleSheet(
            studentCsvImportRequest.getGoogleSheetUrl()
    );

    List<StudentCsvImportResponse.StudentInfo> responseStudents = savedStudents.stream()
            .map(s -> new StudentCsvImportResponse.StudentInfo(
                    s.getProfile().get("name").toString(),
                    s.getEmail()
            ))
            .toList();

    StudentCsvImportResponse response = StudentCsvImportResponse.of(
            savedStudents.size(),
            responseStudents
    );

    return ResponseEntity.ok(response);
  }
}
