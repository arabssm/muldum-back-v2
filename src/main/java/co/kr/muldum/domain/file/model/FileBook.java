package co.kr.muldum.domain.file.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "file_books")
public class FileBook {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "file_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private File file;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "book_config", columnDefinition = "jsonb")
  private Map<String, Object> bookConfig;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

}
