package co.kr.muldum.application.user;

import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.global.util.JwtProvider;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.user.oauth.GoogleOAuthClient;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleUserInfoDto;
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
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;


    public LoginResponseDto loginWithGoogle(String authorizationCode) {

        GoogleUserInfoDto userInfoDto = googleOAuthClient.getUserInfoByCode(authorizationCode);

        String email = userInfoDto.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_DOMAIN);
        }
        if (!email.endsWith("@bssm.hs.kr")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_DOMAIN);
        }

        UserInfo userInfo = userReader.findByEmail(userInfoDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_USER));

        String access = jwtProvider.createAccessToken(userInfo.getUserId(), userInfo.getUserType().name());
        String refresh = jwtProvider.createRefreshToken(userInfo.getUserId(), userInfo.getUserType().name());

        String redisKey = "refresh:" + userInfo.getUserType().name() + ":" + userInfo.getUserId();
        redisTemplate.opsForValue().set(redisKey, refresh, jwtProvider.getRefreshTokenExpirationMillis(), TimeUnit.MILLISECONDS);

        return LoginResponseDto.of(userInfo, access, refresh);
    }
}
