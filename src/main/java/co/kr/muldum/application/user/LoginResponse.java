package co.kr.muldum.application.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String userType;
    private Long userId;
    private String name;
    private Long teamId;
    private String role;
    private String accessToken;
    private String refreshToken;
}
