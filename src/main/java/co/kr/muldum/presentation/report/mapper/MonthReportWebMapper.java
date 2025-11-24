package co.kr.muldum.presentation.report.mapper;

import co.kr.muldum.application.report.dto.response.TeacherMonthReportApplicationResponse;
import co.kr.muldum.application.report.port.in.SaveMonthReportCommand;
import co.kr.muldum.application.report.port.in.SubmitMonthReportCommand;
import co.kr.muldum.domain.report.model.MonthReport;
import co.kr.muldum.presentation.report.dto.request.MonthReportRequest;
import co.kr.muldum.presentation.report.dto.response.MonthReportDetailResponse;
import co.kr.muldum.presentation.report.dto.response.MonthReportSimpleResponse;
import co.kr.muldum.presentation.report.dto.response.TeacherMonthReportDetailResponse;
import co.kr.muldum.presentation.report.dto.response.TeacherMonthReportSimpleResponse;
import org.springframework.stereotype.Component;

@Component
public class MonthReportWebMapper {

    public SaveMonthReportCommand toCommand(MonthReportRequest request, Long userId, Long teamId) {
        return new SaveMonthReportCommand(
                userId,
                teamId,
                request.getTopic(),
                request.getGoal(),
                request.getTech(),
                request.getProblem(),
                request.getTeacherFeedback(),
                request.getMentorFeedback()
        );
    }

    public SubmitMonthReportCommand toSubmitCommand(MonthReportRequest request, Long userId, Long teamId, Long reportId) {
        return new SubmitMonthReportCommand(
                userId,
                teamId,
                reportId,
                request.getTopic(),
                request.getGoal(),
                request.getTech(),
                request.getProblem(),
                request.getTeacherFeedback(),
                request.getMentorFeedback(),
                request.getStatus()
        );
    }

    public MonthReportDetailResponse toDetailResponse(MonthReport report) {
        return MonthReportDetailResponse.builder()
                .reportId(report.getId())
                .topic(report.getTopic())
                .goal(report.getGoal())
                .tech(report.getTech())
                .problem(report.getProblem())
                .teacherFeedback(report.getTeacherFeedback())
                .mentorFeedback(report.getMentorFeedback())
                .status(report.getStatus())
                .feedback(report.getTeacherFeedback())
                .build();
    }

    public MonthReportSimpleResponse toSimpleResponse(MonthReport report) {
        return MonthReportSimpleResponse.builder()
                .reportId(report.getId())
                .topic(report.getTopic())
                .status(report.getStatus())
                .submittedAt(report.getSubmittedAt())
                .build();
    }

    public TeacherMonthReportDetailResponse toTeacherDetailResponse(TeacherMonthReportApplicationResponse report) {
        return TeacherMonthReportDetailResponse.builder()
                .reportId(report.getReportId())
                .teamId(report.getTeamId())
                .name(report.getName())
                .topic(report.getTopic())
                .goal(report.getGoal())
                .tech(report.getTech())
                .problem(report.getProblem())
                .teacherFeedback(report.getTeacherFeedback())
                .mentorFeedback(report.getMentorFeedback())
                .status(report.getStatus())
                .feedback(report.getFeedback())
                .build();
    }

    public TeacherMonthReportSimpleResponse toTeacherSimpleResponse(TeacherMonthReportApplicationResponse report) {
        return TeacherMonthReportSimpleResponse.builder()
                .reportId(report.getReportId())
                .teamId(report.getTeamId())
                .name(report.getName())
                .topic(report.getTopic())
                .status(report.getStatus())
                .submittedAt(report.getSubmittedAt())
                .build();
    }
}
