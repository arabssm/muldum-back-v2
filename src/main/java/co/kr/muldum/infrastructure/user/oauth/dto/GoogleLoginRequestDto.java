package co.kr.muldum.infrastructure.user.oauth.dto;

import lombok.Getter;

@Getter
public class GoogleLoginRequestDto {

    private String authorizationCode;
}
