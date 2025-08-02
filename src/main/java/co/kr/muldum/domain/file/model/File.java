package co.kr.muldum.domain.file.model;

import jakarta.persistence.*;

@Entity
@Table(name = "files")
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //TO DO: erd에 따른 추가 필드 필요
}
