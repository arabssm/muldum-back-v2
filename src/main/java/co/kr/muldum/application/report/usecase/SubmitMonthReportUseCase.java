package co.kr.muldum.application.report.usecase;

import co.kr.muldum.application.report.port.in.SubmitMonthReportCommand;

public interface SubmitMonthReportUseCase {
    void submit(SubmitMonthReportCommand command);
}
