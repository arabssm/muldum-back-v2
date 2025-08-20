package co.kr.muldum.presentation.notice;

import co.kr.muldum.application.notice.query.NoticeQueryService;
import co.kr.muldum.application.notice.query.NoticeSimpleResponse;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("std/notice")
@RequiredArgsConstructor
public class NoticeStudentController {
  private final NoticeQueryService noticeQueryService;

  @GetMapping
  public ResponseEntity<Page<NoticeSimpleResponse>> getNoticeList(
          @AuthenticationPrincipal CustomUserDetails customUserDetails,
          @PageableDefault(size = 10) Pageable pageable
          ) {
    Long teamId = customUserDetails.getTeamId();
    Page<NoticeSimpleResponse> notices = noticeQueryService.getAllNoticesByTeamId(pageable, teamId);

    return ResponseEntity.ok(notices);
  }
}
