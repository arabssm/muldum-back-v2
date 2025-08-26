package co.kr.muldum.domain.notice.repository;

import co.kr.muldum.domain.notice.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  @Query("select n from Notice n " +
          "ORDER BY n.updatedAt DESC")
  Page<Notice> findAllNotice(Pageable pageable);

  @Query("select n from Notice n where n.id = :noticeId")
  Optional<Notice> findNoticeById(@Param("noticeId") Long noticeId);
}
