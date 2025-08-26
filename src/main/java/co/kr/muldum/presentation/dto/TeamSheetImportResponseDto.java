package co.kr.muldum.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO for the Google Sheet team import API.
 * Summarizes processed counts and row-level errors.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamSheetImportResponseDto {
    private int total;
    private int teamsUpserted;
    private int membersUpserted;
    private int studentsUpserted;
    private int skipped;
    private int failed;
    private List<ErrorDetail> errors;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private int row;
        private String reason;
    }
}
