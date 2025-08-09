package co.kr.muldum.domain.notice.repository;

import co.kr.muldum.domain.notice.model.NoticeTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeTeamRepository extends JpaRepository<NoticeTeam, Long> {
  void deleteAllByNoticeId(Long noticeId);
}
