package co.kr.muldum.presentation.notice;

import co.kr.muldum.application.notice.query.NoticeDetailResponse;
import co.kr.muldum.application.notice.query.NoticeQueryService;
import co.kr.muldum.application.notice.query.NoticeSimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ara/notice")
@RequiredArgsConstructor
public class NoticeAraController {

  private final NoticeQueryService noticeQueryService;

  @GetMapping("/health")
  public ResponseEntity<String> getHealth() {
    return ResponseEntity.ok("OK");
  }

  @GetMapping
  public ResponseEntity<Page<NoticeSimpleResponse>> getAllNoticeList(
          @PageableDefault(size = 10) Pageable pageable
  ) {
    Page<NoticeSimpleResponse> notices = noticeQueryService.getAllNotices(pageable);
    return ResponseEntity.ok(notices);
  }

  @GetMapping("/{notice_id:[0-9]+}")  // 숫자만 매칭하도록 정규식 추가
  public ResponseEntity<NoticeDetailResponse> getDetailNotice(
          @PathVariable("notice_id") Long noticeId
  ){
    NoticeDetailResponse noticeDetail = noticeQueryService.getNoticeDetail(noticeId);
    return ResponseEntity.ok(noticeDetail);
  }
}
