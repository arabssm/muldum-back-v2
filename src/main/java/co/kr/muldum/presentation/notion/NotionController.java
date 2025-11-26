package co.kr.muldum.presentation.notion;

import co.kr.muldum.application.teamspace.service.NotionOAuthService;
import co.kr.muldum.application.teamspace.service.NotionService;
import co.kr.muldum.presentation.teamspace.dto.NotionImportRequest;
import co.kr.muldum.presentation.teamspace.dto.NotionOAuthCallbackRequest;
import co.kr.muldum.presentation.teamspace.dto.NotionPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class NotionController {

    private final NotionOAuthService oauthService;
    private final NotionService notionService;

    /**
     * Notion OAuth 콜백 처리
     */
    @PostMapping("/notion/oauth/callback")
    public ResponseEntity<?> handleOAuthCallback(
            @RequestBody NotionOAuthCallbackRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = Long.parseLong(userDetails.getUsername()); // 또는 실제 userId 추출 로직

            oauthService.handleOAuthCallback(request.getCode(), userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notion 연동 완료"
            ));
        } catch (Exception e) {
            log.error("Notion OAuth 콜백 처리 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        }
    }

}
