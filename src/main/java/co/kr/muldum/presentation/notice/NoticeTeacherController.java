package co.kr.muldum.presentation.notice;

import co.kr.muldum.application.notice.command.CreateNoticeRequest;
import co.kr.muldum.application.notice.command.CreateNoticeResponse;
import co.kr.muldum.application.notice.command.NoticeCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("tch/notice")
public class NoticeTeacherController {

  private final NoticeCommandService noticeCommandService;

  @PostMapping()
  public ResponseEntity<CreateNoticeResponse> createNotice(
          @RequestBody CreateNoticeRequest createNoticeRequest
          // TO DO: @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    Long fakeUserId = 1L; //TO DO: Security 연동 후 리팩터링
    Long noticeId = noticeCommandService.createNotice(createNoticeRequest, fakeUserId);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CreateNoticeResponse.builder()
                    .id(noticeId)
                    .message("공지사항이 성공적으로 등록되었습니다.")
                    .build());
  }
}
