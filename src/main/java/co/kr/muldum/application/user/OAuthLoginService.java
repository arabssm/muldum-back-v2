package co.kr.muldum.application.user;

import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.global.exception.UnauthorizedDomainException;
import co.kr.muldum.global.util.JwtProvider;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.user.oauth.GoogleOAuthClient;
import co.kr.muldum.infrastructure.user.oauth.KakaoOAuthClient;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleUserInfoDto;
import co.kr.muldum.infrastructure.user.oauth.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final GoogleOAuthClient googleOAuthClient;
    private final UserReader userReader;
    private final JwtProvider jwtProvider =  new JwtProvider();
    private final RedisTemplate<String, String> redisTemplate;

    public LoginResponseDto loginWithGoogle(String accessToken) {

        // 구글에서 사용자 정보 받아오기
        GoogleUserInfoDto userInfoDto = googleOAuthClient.getUserInfo(accessToken);

        String email = userInfoDto.getEmail();

        if (!email.endsWith("@bssm.hs.kr") && !email.endsWith("@gmail.com")) {
            throw new UnauthorizedDomainException("허용되지 않은 이메일 도메인입니다.");
        }

        // email 기반으로 DB에서 사용자 조회
        UserInfo userInfo = userReader.findByEmail(userInfoDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_USER));

        // 토큰 발급
        String access = jwtProvider.createAccessToken(userInfo.getUserId(), userInfo.getUserType().name());
        String refresh = jwtProvider.createRefreshToken(userInfo.getUserId(), userInfo.getUserType().name());

        String redisKey = "refresh:" + userInfo.getUserType().name() + ":" + userInfo.getUserId();
        redisTemplate.opsForValue().set(redisKey, refresh, jwtProvider.getRefreshTokenExpirationMillis(), TimeUnit.MILLISECONDS);

        // 응답 DTO
        return LoginResponseDto.of(userInfo, access, refresh);
    }
}
