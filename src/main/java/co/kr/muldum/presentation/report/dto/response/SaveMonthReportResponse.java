package co.kr.muldum.presentation.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveMonthReportResponse {
    private Long reportId;
    private String message;
}
