package co.kr.muldum.domain.notice.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class NoticeTeamId implements Serializable {
  private Long noticeId;
  private Long teamId;

  protected NoticeTeamId() {}

  public NoticeTeamId(Long noticeId, Long teamId) {
    this.noticeId = noticeId;
    this.teamId = teamId;
  }

  public Long getNoticeId() {
    return noticeId;
  }
  public Long getTeamId() {
    return teamId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NoticeTeamId that)) return false;
    return noticeId.equals(that.noticeId) && teamId.equals(that.teamId);
  }
  @Override
  public int hashCode() {
    return Objects.hash(noticeId, teamId);
  }

}
