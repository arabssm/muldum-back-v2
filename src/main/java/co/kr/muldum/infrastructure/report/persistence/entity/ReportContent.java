package co.kr.muldum.infrastructure.report.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportContent {
    private String topic;
    private String goal;
    private String tech;
    private String problem;
    private String teacherFeedback;
    private String mentorFeedback;
}
