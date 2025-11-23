package co.kr.muldum.application.report.dto.response;

import co.kr.muldum.domain.report.model.ReportStatus;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeacherMonthReportApplicationResponse {
    private Long reportId;
    private Long userId;
    private Long teamId;
    private String name;
    private String topic;
    private List<String> goal;
    private String tech;
    private String problem;
    private String teacherFeedback;
    private String mentorFeedback;
    private ReportStatus status;
    private LocalDateTime submittedAt;
    private Integer score;
}
