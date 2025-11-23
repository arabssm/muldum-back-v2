package co.kr.muldum.presentation.report.dto.response;

import co.kr.muldum.domain.report.model.ReportStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MonthReportDetailResponse {
    private Long reportId;
    private String topic;
    private String goal;
    private String tech;
    private String problem;
    private String teacherFeedback;
    private String mentorFeedback;
    private ReportStatus status;
    private String feedback;
}
