package co.kr.muldum.domain.notice.repository;

import co.kr.muldum.domain.notice.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  @Query("SELECT n FROM Notice n " +
          "ORDER BY COALESCE(n.updatedAt) DESC")
  Page<Notice> getAllOrderByUpdatedAt(Pageable pageable);
}
