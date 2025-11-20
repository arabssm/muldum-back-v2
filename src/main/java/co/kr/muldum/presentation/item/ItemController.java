package co.kr.muldum.presentation.item;

import co.kr.muldum.domain.item.dto.res.GetItemGuideResponse;
import co.kr.muldum.domain.item.service.ItemRequestService;
import co.kr.muldum.domain.item.service.TeacherItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final TeacherItemService teacherItemService;
    private final ItemRequestService itemRequestService;

    @PostMapping("/items/issue")
    public ResponseEntity<String> fixNthIssues() {
        log.info("물품신청 n차 문제 해결 요청 접수");

        String response = teacherItemService.fixNthIssues();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ara/items/guide/{guide_id}")
    public ResponseEntity<GetItemGuideResponse> getOneItemGuide(
            @PathVariable("guide_id") Long guideId,
            @RequestParam("type") String projectType
    ) {
        log.info("물품 가이드 조회 요청 - guideId: {}, projectType: {}", guideId, projectType);

        GetItemGuideResponse itemGuide = itemRequestService.getItemGuide(guideId, projectType);

        return ResponseEntity.ok(itemGuide);
    }
}
