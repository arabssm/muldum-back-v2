package co.kr.muldum.presentation.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScoreMonthReportRequest {

    @NotBlank
    private String feedback;
}
