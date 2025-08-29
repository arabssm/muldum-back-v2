package co.kr.muldum.presentation.notice;

import co.kr.muldum.application.notice.command.CreateNoticeRequest;
import co.kr.muldum.application.notice.command.CreateNoticeResponse;
import co.kr.muldum.application.notice.command.DeleteNoticeResponse;
import co.kr.muldum.application.notice.command.NoticeCommandService;
import co.kr.muldum.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("tch/notice")
@Slf4j
@PreAuthorize("hasRole('TEACHER')")
public class NoticeTeacherController {

  private final NoticeCommandService noticeCommandService;

  @PostMapping
  public ResponseEntity<CreateNoticeResponse> createNotice(
          @Valid @RequestBody CreateNoticeRequest createNoticeRequest,
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

  @PatchMapping("/{notice_id}")
  public ResponseEntity<CreateNoticeResponse> updateNotice(
          @PathVariable("notice_id") Long noticeId,
          @Valid @RequestBody CreateNoticeRequest createNoticeRequest,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ) throws AccessDeniedException {
    noticeCommandService.updateNotice(noticeId, createNoticeRequest, customUserDetails.getUserId());
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(CreateNoticeResponse.builder()
                    .id(noticeId)
                    .message(NoticeMessage.NOTICE_UPDATED_SUCCESS.getMessage())
                    .build()
            );
  }

  @DeleteMapping("/{notice_id}")
  public ResponseEntity<DeleteNoticeResponse> deleteNotice(
          @PathVariable("notice_id") Long noticeId,
          @AuthenticationPrincipal CustomUserDetails customUserDetails
  ){
    noticeCommandService.deleteNotice(noticeId, customUserDetails.getUserId());
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(DeleteNoticeResponse.builder()
                    .message(NoticeMessage.NOTICE_DELETED_SUCCESS.getMessage())
                    .build()
            );
  }
}
