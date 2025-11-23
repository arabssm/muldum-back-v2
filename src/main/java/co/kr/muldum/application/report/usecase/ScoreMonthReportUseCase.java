package co.kr.muldum.application.report.usecase;

public interface ScoreMonthReportUseCase {
    void score(Long reportId, String feedback, Long teacherId);
}
