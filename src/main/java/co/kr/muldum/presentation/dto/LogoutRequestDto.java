package co.kr.muldum.presentation.dto;

import co.kr.muldum.domain.user.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogoutRequestDto {

    private String refreshToken;
    private UserType userType;
    private Long userId;
}
