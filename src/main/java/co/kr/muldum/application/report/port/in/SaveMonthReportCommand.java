package co.kr.muldum.application.report.port.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class SaveMonthReportCommand {
    private final Long userId;
    private final Long teamId;
    private final String topic;
    private final List<String> goal;
    private final String tech;
    private final String problem;
    private final String teacherFeedback;
    private final String mentorFeedback;
}
