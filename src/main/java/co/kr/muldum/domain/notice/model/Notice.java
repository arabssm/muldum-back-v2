package co.kr.muldum.domain.notice.model;

import co.kr.muldum.domain.notice.model.enums.Status;
import co.kr.muldum.domain.user.model.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "author_user_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Teacher teacher;

  private String title;

  @Convert(converter = ContentDataConverter.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "content_data")
  private ContentData contentData;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(name = "notification_config", columnDefinition = "VARCHAR(255)")
  private String notificationConfig;

  @Column(name = "deadline_date")
  private LocalDate deadlineDate;

  @Builder.Default
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();
}
