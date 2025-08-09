package co.kr.muldum.domain.notice.model;

import co.kr.muldum.domain.notice.exception.NoticeOrTeamIdNullException;
import co.kr.muldum.domain.teamspace.model.Team;
import jakarta.persistence.*;

@Entity
@Table(name = "notice_team")
public class NoticeTeam {
  @EmbeddedId
  private NoticeTeamId id;

  @ManyToOne
  @MapsId("noticeId")
  @JoinColumn(name = "notice_id")
  private Notice notice;

  @ManyToOne
  @MapsId("teamId")
  @JoinColumn(name = "team_id")
  private Team team;

  protected NoticeTeam() {}

  public NoticeTeam(Notice notice, Team team) {
    this.notice = notice;
    this.team = team;
    if (notice.getId() == null || team.getId() == null) {
      throw new NoticeOrTeamIdNullException("noticeId 또는 teamId가 null입니다.");
    }
    this.id = new NoticeTeamId(notice.getId(), team.getId());
  }

}
