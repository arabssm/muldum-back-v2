package co.kr.muldum.domain.report.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class MonthReport {

    private final Long id;
    private final Long userId;
    private final Long teamId;
    private final String topic;
    private final List<String> goal;
    private final String tech;
    private final String problem;
    private final String teacherFeedback;
    private final String mentorFeedback;
    private final ReportStatus status;
    private final LocalDateTime submittedAt;
    private final String feedback;
    private final LocalDateTime createdAt;
}
