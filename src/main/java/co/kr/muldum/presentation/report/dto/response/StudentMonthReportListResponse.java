package co.kr.muldum.presentation.report.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentMonthReportListResponse {
    private int month;
    private List<MonthReportSimpleResponse> reports;
}
