package co.kr.muldum.application.notice.query;

import co.kr.muldum.domain.notice.exception.NotFoundException;
import co.kr.muldum.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService {
  private final NoticeRepository noticeRepository;

  public Page<NoticeSimpleResponse> getAllNotices(Pageable pageable) {
    return noticeRepository.findAllNotice(pageable)
            .map(NoticeSimpleResponse::fromEntity);
  }

  public NoticeDetailResponse getNoticeDetail(Long noticeId) {
    return noticeRepository.findNoticeById(noticeId)
            .map(NoticeDetailResponse::fromEntity)
            .orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다. id=" + noticeId));
  }
}
