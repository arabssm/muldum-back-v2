package co.kr.muldum.domain.file.repository;

import co.kr.muldum.domain.file.model.FileBook;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileBookRepository extends JpaRepository<FileBook, Long> {
  List<FileBook> findAllByNoticeId(Long noticeId);

  @Modifying
  @Transactional
  void deleteAllByNoticeId(@Param("noticeId") Long noticeId);
}
