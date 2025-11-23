package co.kr.muldum.infrastructure.report.persistence.adapter;

import co.kr.muldum.domain.report.model.MonthReport;
import co.kr.muldum.infrastructure.report.persistence.entity.MonthReportEntity;
import co.kr.muldum.infrastructure.report.persistence.entity.ReportContent;
import org.springframework.stereotype.Component;

@Component
public class MonthReportMapper {

    public MonthReport toDomain(MonthReportEntity entity) {
        ReportContent content = entity.getReportContent();
        return MonthReport.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .teamId(entity.getTeamId())
                .topic(content != null ? content.getTopic() : null)
                .goal(content != null ? content.getGoal() : null)
                .tech(content != null ? content.getTech() : null)
                .problem(content != null ? content.getProblem() : null)
                .teacherFeedback(content != null ? content.getTeacherFeedback() : null)
                .mentorFeedback(content != null ? content.getMentorFeedback() : null)
                .status(entity.getStatus())
                .submittedAt(entity.getSubmittedAt())
                .score(entity.getScore())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public MonthReportEntity toEntity(MonthReport domain) {
        ReportContent content = ReportContent.builder()
                .topic(domain.getTopic())
                .goal(domain.getGoal())
                .tech(domain.getTech())
                .problem(domain.getProblem())
                .teacherFeedback(domain.getTeacherFeedback())
                .mentorFeedback(domain.getMentorFeedback())
                .build();

        return MonthReportEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .teamId(domain.getTeamId())
                .reportContent(content)
                .status(domain.getStatus())
                .submittedAt(domain.getSubmittedAt())
                .score(domain.getScore())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
