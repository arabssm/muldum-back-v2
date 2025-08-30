package co.kr.muldum.application.notice.query;

import co.kr.muldum.domain.notice.model.Notice;

import java.time.LocalDateTime;

public record NoticeSimpleResponse(
        Long id,
        String title,
        LocalDateTime updatedAt,
        String teacher
) {
  public static NoticeSimpleResponse fromEntity(Notice notice) {
    return new NoticeSimpleResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getUpdatedAt(),
            notice.getUser().getName()
    );
  }
}
