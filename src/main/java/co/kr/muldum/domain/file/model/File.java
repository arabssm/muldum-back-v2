package co.kr.muldum.domain.file.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "files")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String path;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> metadata;

  @Column(name = "owner_user_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserType ownerUserType;

  @Column(name = "owner_user_id", nullable = false)
  private Integer ownerUserId;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public enum UserType {
    STUDENT, TEACHER, MENTOR // TODO: 유저 타입 enum 머지 시 리팩토링
  }
}
