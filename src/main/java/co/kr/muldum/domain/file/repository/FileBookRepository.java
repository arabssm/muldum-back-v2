package co.kr.muldum.domain.file.repository;

import co.kr.muldum.domain.file.model.FileBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileBookRepository extends JpaRepository<FileBook, Long> {
}
