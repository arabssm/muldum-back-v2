package co.kr.muldum.presentation.urge;

import co.kr.muldum.application.urge.dto.UrgeRequest;
import co.kr.muldum.application.urge.service.UrgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "독촉 API", description = "독촉 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UrgeController {

    private final UrgeService urgeService;

    @Operation(summary = "독촉 메시지 보내기", description = "지정한 사용자에게 독촉 메시지를 이메일로 보냅니다.")
    @PostMapping("/urge")
    public ResponseEntity<Void> sendUrgeEmail(@RequestBody UrgeRequest urgeRequest) {
        urgeService.sendUrgeEmail(urgeRequest);
        return ResponseEntity.ok().build();
    }
}
