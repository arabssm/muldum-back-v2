package co.kr.muldum.presentation.notice;

import co.kr.muldum.application.notice.query.NoticeQueryService;
import co.kr.muldum.application.notice.query.NoticeSimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ara/notice")
@RequiredArgsConstructor
public class NoticeAraController {

  private final NoticeQueryService noticeQueryService;

  @GetMapping
  public ResponseEntity<Page<NoticeSimpleResponse>> getGeneralNoticeList(
          @PageableDefault(size = 10) Pageable pageable
  ) {
    Page<NoticeSimpleResponse> notices = noticeQueryService.getAllNoticesByTeamId(pageable, null);
    return ResponseEntity.ok(notices);
  }
}
