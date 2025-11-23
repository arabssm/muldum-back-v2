package co.kr.muldum.application.report.usecase;

import co.kr.muldum.application.report.port.out.LoadMonthReportPort;
import co.kr.muldum.domain.report.model.MonthReport;
import co.kr.muldum.domain.report.model.ReportStatus;
import co.kr.muldum.global.exception.MonthReportNotFoundException;
import co.kr.muldum.presentation.report.dto.response.MonthReportDetailResponse;
import co.kr.muldum.presentation.report.mapper.MonthReportWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetDraftReportUseCase {

    private final LoadMonthReportPort loadMonthReportPort;
    private final MonthReportWebMapper monthReportWebMapper;

    public MonthReportDetailResponse getDraftReport(Long teamId) {
        MonthReport monthReport = loadMonthReportPort.findByTeamIdAndStatus(teamId, ReportStatus.DRAFT)
                .orElseThrow(() -> new MonthReportNotFoundException(null));
        return monthReportWebMapper.toDetailResponse(monthReport);
    }
}
