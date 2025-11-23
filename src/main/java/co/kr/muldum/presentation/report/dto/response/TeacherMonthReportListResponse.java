package co.kr.muldum.presentation.report.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeacherMonthReportListResponse {
    private Integer month;
    private List<TeacherMonthReportSimpleResponse> reports;
}
