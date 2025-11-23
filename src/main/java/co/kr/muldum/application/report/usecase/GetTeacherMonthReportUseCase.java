package co.kr.muldum.application.report.usecase;

import co.kr.muldum.application.report.dto.response.TeacherMonthReportApplicationResponse;

import java.util.List;

public interface GetTeacherMonthReportUseCase {
    TeacherMonthReportApplicationResponse getTeacherByReportId(Long reportId, Long teacherId);
    List<TeacherMonthReportApplicationResponse> getByTeamAndMonth(Long teamId, Integer month, Long teacherId);
}
