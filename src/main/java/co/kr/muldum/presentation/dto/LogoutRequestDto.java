package co.kr.muldum.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import jakarta.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class LogoutRequestDto {

    @NotBlank
    private String refreshToken;
}
