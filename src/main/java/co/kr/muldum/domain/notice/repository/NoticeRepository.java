package co.kr.muldum.domain.notice.repository;

import co.kr.muldum.domain.notice.model.Notice;
import co.kr.muldum.domain.notice.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  @EntityGraph(attributePaths = "teacher")
  @Query("SELECT n FROM Notice n " +
          "ORDER BY n.updatedAt DESC")
  Page<Notice> getAllOrderByUpdatedAt(Pageable pageable);

  @EntityGraph(attributePaths = "teacher")
  @Query("select n from Notice n " +
          "where n.status = :status " +
          "ORDER BY n.updatedAt DESC")
  Page<Notice> findGeneralNotice(@Param("status") Status status, Pageable pageable);

  @Query("SELECT n FROM NoticeTeam nt JOIN nt.notice n " +
          "WHERE nt.team = :teamId " +
          "ORDER BY COALESCE(n.updatedAt) DESC")
  Page<Notice> findAllNoticeByTeamId(Long teamId, Pageable pageable);
}
