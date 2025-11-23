package co.kr.muldum.application.report.port.in;

import co.kr.muldum.domain.report.model.ReportStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class SubmitMonthReportCommand {
    private final Long userId;
    private final Long teamId;
    private final Long reportId;
    private final String topic;
    private final List<String> goal;
    private final String tech;
    private final String problem;
    private final String teacherFeedback;
    private final String mentorFeedback;
    private final ReportStatus status;
}
