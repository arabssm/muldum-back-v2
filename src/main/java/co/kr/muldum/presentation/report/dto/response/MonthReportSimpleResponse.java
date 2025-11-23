package co.kr.muldum.presentation.report.dto.response;

import co.kr.muldum.domain.report.model.ReportStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MonthReportSimpleResponse {
    private Long reportId;
    private String topic;
    private ReportStatus status;
    private LocalDateTime submittedAt;
}
