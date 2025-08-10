package co.kr.muldum.domain.notice.repository;

import co.kr.muldum.domain.notice.model.NoticeTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface NoticeTeamRepository extends JpaRepository<NoticeTeam, Long> {
  @Modifying
  void deleteAllByNoticeId(Long noticeId);
}
