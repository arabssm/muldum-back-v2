package co.kr.muldum.presentation.user;

import co.kr.muldum.application.user.TeacherImportService;
import co.kr.muldum.application.user.dto.TeacherImportResponse;
import co.kr.muldum.application.user.dto.TeacherSheetImportRequest;
import co.kr.muldum.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tch/import")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class UserImportController {

    private final TeacherImportService teacherImportService;

    @PostMapping("/sheet")
    public ResponseEntity<TeacherImportResponse> importTeachersFromSheet(
            @RequestBody TeacherSheetImportRequest importRequest) {
        List<User> savedTeachers = teacherImportService.importFromGoogleSheet(importRequest.getGoogleSheetUrl());
        return createResponse(savedTeachers);
    }

    @PostMapping("/csv")
    public ResponseEntity<TeacherImportResponse> importTeachersFromCsv(
            @RequestParam("file") MultipartFile file) {
        List<User> savedTeachers = teacherImportService.importFromCsv(file);
        return createResponse(savedTeachers);
    }

    private ResponseEntity<TeacherImportResponse> createResponse(List<User> savedTeachers) {
        List<TeacherImportResponse.TeacherInfo> responseTeachers = savedTeachers.stream()
                .map(s -> new TeacherImportResponse.TeacherInfo(
                        s.getName(),
                        s.getEmail()))
                .toList();

        TeacherImportResponse response = TeacherImportResponse.of(
                savedTeachers.size(),
                responseTeachers);

        return ResponseEntity.ok(response);
    }
}
