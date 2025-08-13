package co.kr.muldum.domain.notice.model;

import co.kr.muldum.application.notice.command.CreateNoticeRequest;
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
import java.util.List;
import java.util.Objects;

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

  public void updateNotice(CreateNoticeRequest createNoticeRequest) {
    validateRequest(createNoticeRequest);

    this.title = createNoticeRequest.getTitle();
    this.contentData = new ContentData(
        createNoticeRequest.getContent(),
        createNoticeRequest.getFiles() == null ? List.of() :
            createNoticeRequest.getFiles().stream()
                    .filter(Objects::nonNull)
                    .map(file -> new FileData(file.getUrl()))
                    .toList()
    );
    this.status = createNoticeRequest.getStatus();
    this.deadlineDate = createNoticeRequest.getDeadlineDate();
  }

  private void validateRequest(CreateNoticeRequest request) {
    if (request.getTitle() == null || request.getTitle().isBlank()) {
      throw new IllegalArgumentException("공지 제목은 필수입니다.");
    }
    if (request.getStatus() == null) {
      throw new IllegalArgumentException("공지 상태는 필수입니다.");
    }
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
