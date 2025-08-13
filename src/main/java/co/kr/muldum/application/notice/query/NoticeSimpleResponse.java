package co.kr.muldum.application.notice.query;

import co.kr.muldum.domain.notice.model.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeSimpleResponse {
  private final Long id;
  private final String title;
  private final LocalDateTime updatedAt;
  private Long authorUserId;

  public NoticeSimpleResponse(Long id, String title, LocalDateTime updatedAt, Long authorUserId) {
    this.id = id;
    this.title = title;
    this.updatedAt = updatedAt;
  }
  public static NoticeSimpleResponse fromEntity(Notice notice) {
    return new NoticeSimpleResponse(
        notice.getId(),
        notice.getTitle(),
        notice.getUpdatedAt(),
        notice.getTeacher().getId()
    );
  }
}
