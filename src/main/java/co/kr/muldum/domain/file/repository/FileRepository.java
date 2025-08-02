package co.kr.muldum.domain.file.repository;

import co.kr.muldum.domain.file.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
