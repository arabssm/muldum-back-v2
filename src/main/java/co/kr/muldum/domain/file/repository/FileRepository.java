package co.kr.muldum.domain.file.repository;

import co.kr.muldum.domain.file.model.File;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
  Optional<File> findByPath(String path);

  @Modifying
  @Transactional
  @Query("DELETE FROM File f WHERE f IN :files")
  void deleteAllByFiles(@Param("files") List<File> files);
}
