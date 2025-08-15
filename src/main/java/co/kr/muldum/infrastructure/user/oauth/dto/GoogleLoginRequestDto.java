package co.kr.muldum.infrastructure.user.oauth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginRequestDto {
    @NotBlank(message = "authorizationCode는 필수입니다.")
    private String authorizationCode;
}
