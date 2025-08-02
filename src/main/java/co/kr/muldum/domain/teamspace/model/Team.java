package co.kr.muldum.domain.teamspace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "teams")
@Getter
public class Team {
  @Id
  private Long id;

  // TO DO: erd에 따른 추가 필드 필요
  // 예: 팀 이름, 설명, 생성일 등
}
