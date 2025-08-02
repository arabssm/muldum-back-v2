package co.kr.muldum.domain.notice.repository;

import co.kr.muldum.domain.notice.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
