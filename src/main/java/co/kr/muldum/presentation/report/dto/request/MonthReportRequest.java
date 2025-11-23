package co.kr.muldum.presentation.report.dto.request;

import co.kr.muldum.domain.report.model.ReportStatus;
import lombok.Getter;
import java.util.List;

@Getter
public class MonthReportRequest {
    private String topic;
    private List<String> goal;
    private String tech;
    private String problem;
    private String teacherFeedback;
    private String mentorFeedback;
    private ReportStatus status;
}
