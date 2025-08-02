package co.kr.muldum.domain.file.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "file_books")
public class FileBook {

  @Id
  private Long id;

  // TO DO: erd에 따른 추가 필드 필요
}
