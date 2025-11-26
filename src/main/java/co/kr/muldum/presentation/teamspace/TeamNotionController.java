package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.service.NotionService;
import co.kr.muldum.presentation.teamspace.dto.NotionImportRequest;
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
@RequestMapping("/api/teams")
@Slf4j
@RequiredArgsConstructor
public class TeamNotionController {

    private final NotionService notionService;

    /**
     * Notion 페이지 가져오기 (OAuth)
     */
    @PostMapping("/{teamId}/import-notion-oauth")
    public ResponseEntity<?> importNotionPageWithOAuth(
            @PathVariable Long teamId,
            @RequestBody NotionImportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 현재 로그인한 사용자 ID 추출
            Long userId = extractUserId(userDetails);

            log.info("Notion 페이지 가져오기 요청 - teamId: {}, pageId: {}, userId: {}",
                    teamId, request.getPageId(), userId);

            // Notion API 호출
            NotionPageResponse response = notionService.getNotionPageWithOAuth(
                    request.getPageId(),
                    userId
            );

            log.info("Notion 페이지 가져오기 성공 - title: {}", response.getTitle());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Notion 페이지 가져오기 실패", e);

            // Notion 연동이 안 되어 있으면 401 반환
            if (e.getMessage().contains("연동이 필요")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "error", "Notion 로그인이 필요합니다"
                        ));
            }
            if (e.getMessage().contains("페이지를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "error", e.getMessage()
                        ));
            }
            if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "success", false,
                                "error", e.getMessage()
                        ));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("서버 에러", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "서버 에러가 발생했습니다"
                    ));
        }
    }

    private Long extractUserId(UserDetails userDetails) {
        // 실제 프로젝트의 UserDetails 구조에 맞게 수정
        return Long.parseLong(userDetails.getUsername());
    }
}
