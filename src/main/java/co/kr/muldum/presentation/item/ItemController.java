package co.kr.muldum.presentation.item;

import co.kr.muldum.domain.item.service.TeacherItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final TeacherItemService teacherItemService;

    @PostMapping("/items/issue")
    public ResponseEntity<String> fixNthIssues() {
        log.info("물품신청 n차 문제 해결 요청 접수");

        String response = teacherItemService.fixNthIssues();

        return ResponseEntity.ok(response);
    }
}
