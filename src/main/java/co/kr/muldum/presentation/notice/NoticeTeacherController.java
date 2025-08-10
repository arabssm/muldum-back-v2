package co.kr.muldum.presentation.notice;

import co.kr.muldum.application.notice.command.CreateNoticeRequest;
import co.kr.muldum.application.notice.command.CreateNoticeResponse;
import co.kr.muldum.application.notice.command.NoticeCommandService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("tch/notice")
public class NoticeTeacherController {

  private final NoticeCommandService noticeCommandService;

  @PostMapping()
  public ResponseEntity<CreateNoticeResponse> createNotice(
          @RequestBody CreateNoticeRequest createNoticeRequest,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    Long noticeId = noticeCommandService.createNotice(createNoticeRequest, customUserDetails.getUserId());
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CreateNoticeResponse.builder()
                    .id(noticeId)
                    .message(NoticeMessage.NOTICE_CREATED_SUCCESS.getMessage())
                    .build());
  }

  @DeleteMapping("/{notice_id}")
  public ResponseEntity<String> deleteNotice(
          @PathVariable("notice_id") Long noticeId,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) {
    noticeCommandService.deleteNotice(noticeId, customUserDetails.getUserId());
    return ResponseEntity
            .ok(NoticeMessage.NOTICE_DELETED_SUCCESS.getMessage());
  }
}
