package co.kr.muldum.application.report.port.out;

import co.kr.muldum.domain.report.model.MonthReport;

public interface SaveMonthReportPort {
    MonthReport save(MonthReport monthReport);
}
