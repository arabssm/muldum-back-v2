package co.kr.muldum.application.report.usecase;

import co.kr.muldum.application.report.port.in.SaveMonthReportCommand;
import co.kr.muldum.domain.report.model.MonthReport;

public interface SaveMonthReportUseCase {
    MonthReport save(SaveMonthReportCommand command);
}
