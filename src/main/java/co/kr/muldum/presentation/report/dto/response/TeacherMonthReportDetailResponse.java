package co.kr.muldum.presentation.report.dto.response;

import co.kr.muldum.domain.report.model.ReportStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeacherMonthReportDetailResponse {
    private Long reportId;
    private Long teamId;
    private String name; // user name
    private String topic;
    private String goal;
    private String tech;
    private String problem;
    private String teacherFeedback;
    private String mentorFeedback;
    private ReportStatus status;
}
