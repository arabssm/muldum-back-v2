package co.kr.muldum.infrastructure.report.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportContent {
    private String topic;
    private List<String> goal;
    private String tech;
    private String problem;
    private String teacherFeedback;
    private String mentorFeedback;
}
