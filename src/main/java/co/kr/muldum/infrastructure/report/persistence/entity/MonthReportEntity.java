package co.kr.muldum.infrastructure.report.persistence.entity;

import co.kr.muldum.domain.report.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "month_report")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long teamId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "report_content", columnDefinition = "jsonb")
    private String reportContent;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private LocalDateTime submittedAt;

    private int score;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
