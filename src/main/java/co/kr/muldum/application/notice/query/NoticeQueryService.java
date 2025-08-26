package co.kr.muldum.application.notice.query;

import co.kr.muldum.domain.notice.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeQueryService {
  private final NoticeRepository noticeRepository;

  public Page<NoticeSimpleResponse> getAllNotices(Pageable pageable) {
    return noticeRepository.findAllNotice(pageable)
            .map(NoticeSimpleResponse::fromEntity);
  }

  public NoticeDetailResponse getNoticeDetail(Long noticeId) {
    return noticeRepository.findNoticeById(noticeId)
            .map(NoticeDetailResponse::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. id=" + noticeId));
  }
}
