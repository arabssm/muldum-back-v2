package co.kr.muldum.application.report.port.out;

import co.kr.muldum.domain.report.model.MonthReport;
import co.kr.muldum.domain.report.model.ReportStatus;

import java.util.List;
import java.util.Optional;

public interface LoadMonthReportPort {
    Optional<MonthReport> findById(Long reportId);
    List<MonthReport> findAll();
    List<MonthReport> findByUserId(Long userId);
    List<MonthReport> findByTeamAndMonth(Long teamId, int month); // This might need adjustment depending on domain model
    List<MonthReport> findByMonth(int month);
    Optional<MonthReport> findByUserIdAndMonth(Long userId, int month);
    Optional<MonthReport> findByTeamIdAndStatus(Long teamId, ReportStatus status);
    List<MonthReport> findByTeamId(Long teamId);
}
