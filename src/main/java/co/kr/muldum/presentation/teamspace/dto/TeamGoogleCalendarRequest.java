package co.kr.muldum.presentation.teamspace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamGoogleCalendarRequest {

    @NotBlank(message = "구글 캘린더 ID를 입력해주세요.")
    private String googleCalendarId;
}
