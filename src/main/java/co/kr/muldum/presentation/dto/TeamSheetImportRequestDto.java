package co.kr.muldum.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamSheetImportRequestDto {
    private String sheetLink;
    private String range;
}
