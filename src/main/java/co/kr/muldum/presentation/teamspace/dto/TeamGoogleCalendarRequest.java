package co.kr.muldum.presentation.teamspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamGoogleCalendarRequest {

    @NotBlank(message = "구글 캘린더 ID를 입력해주세요.")
    @Pattern(
        regexp = "^[A-Za-z0-9]+@[A-Za-z0-9]+\\.[A-Za-z0-9.]+$",
        message = "올바른 구글 캘린더 ID 형식이 아닙니다. (예: example@group.calendar.google.com)"
    )
    private String googleCalendarId;
}
