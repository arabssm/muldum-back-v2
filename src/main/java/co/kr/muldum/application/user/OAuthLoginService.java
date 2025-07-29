package co.kr.muldum.application.user;


import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.global.util.JwtProvider;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.user.oauth.GoogleOAuthClient;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final GoogleOAuthClient googleOAuthClient;
    private final UserReader userReader;
    private final JwtProvider jwtProvider;

    public LoginResponseDto loginWithGoogle(String accessToken) {

        // 구글에서 사용자 정보 받아오기
        GoogleUserInfoDto userInfoDto = googleOAuthClient.getUserInfo(accessToken);

        // email 기반으로 DB에서 사용자 조회
        UserInfo userInfo = userReader.findByEmail(userInfoDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_USER));

        // 토큰 발급
        String access = jwtProvider.createAccessToken(userInfo.getUserId(), userInfo.getUserType().name());
        String refresh = jwtProvider.createRefreshToken(userInfo.getUserId(), userInfo.getUserType().name());

        // 응답 DTO
        return LoginResponseDto.of(userInfo, access, refresh);
    }
}
