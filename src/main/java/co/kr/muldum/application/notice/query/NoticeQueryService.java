package co.kr.muldum.application.notice.query;

import co.kr.muldum.domain.notice.repository.NoticeRepository;
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
}
