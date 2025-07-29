package co.kr.muldum.application.user;

import co.kr.muldum.domain.user.model.UserInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private String userType;
    private Long userId;
    private String name;
    private Long teamId;
    private String role;
    private String accessToken;
    private String refreshToken;

    public static LoginResponseDto of(UserInfo userInfo, String accessToken, String refreshToken) {
        return LoginResponseDto.builder()
                .userType(userInfo.getUserType().name())
                .userId(userInfo.getUserId())
                .name(userInfo.getName())
                .teamId(userInfo.getTeamId())
                .role(userInfo.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
