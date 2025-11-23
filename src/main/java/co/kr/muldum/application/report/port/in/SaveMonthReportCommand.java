package co.kr.muldum.application.report.port.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaveMonthReportCommand {
    private final Long userId;
    private final Long teamId;
    private final String topic;
    private final String goal;
    private final String tech;
    private final String problem;
    private final String teacherFeedback;
    private final String mentorFeedback;
}
