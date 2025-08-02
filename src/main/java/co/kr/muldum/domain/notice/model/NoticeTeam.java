package co.kr.muldum.domain.notice.model;

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
    this.id = new NoticeTeamId(notice.getId(), team.getId());
  }

}
