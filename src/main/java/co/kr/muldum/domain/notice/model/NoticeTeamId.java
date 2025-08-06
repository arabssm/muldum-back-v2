package co.kr.muldum.domain.notice.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class NoticeTeamId implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private Long noticeId;
  private Long teamId;

  protected NoticeTeamId() {}

  public NoticeTeamId(Long noticeId, Long teamId) {
    this.noticeId = noticeId;
    this.teamId = teamId;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof NoticeTeamId that)) return false;
    if (this == o) return true;
    return noticeId.equals(that.noticeId) && teamId.equals(that.teamId);
  }
  @Override
  public int hashCode() {
    return Objects.hash(noticeId, teamId);
  }

}
