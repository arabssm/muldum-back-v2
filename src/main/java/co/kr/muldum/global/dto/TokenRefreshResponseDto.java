package co.kr.muldum.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRefreshResponseDto {
    private String accessToken;
}
