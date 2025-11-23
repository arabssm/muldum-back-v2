package co.kr.muldum.application.report.usecase;

import co.kr.muldum.domain.report.model.MonthReport;

import java.util.List;

public interface GetStudentMonthReportUseCase {
    MonthReport getByReportId(Long reportId, Long userId, Long teamId);
    List<MonthReport> getByUserId(Long userId, Long teamId);
}
