package co.kr.muldum.domain.teamspace.model;

import co.kr.muldum.domain.item.model.RequestDetails;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "teams")
@Getter
public class Team {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "team_config", columnDefinition = "jsonb")
    private TeamConfig teamConfig;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = true)
  private LocalDateTime updatedAt;
}
