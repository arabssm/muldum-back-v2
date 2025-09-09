package co.kr.muldum.application.user;

import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.global.util.JwtProvider;
import co.kr.muldum.infrastructure.user.oauth.GoogleOAuthClient;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleUserInfoDto;
import co.kr.muldum.infrastructure.user.oauth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final GoogleOAuthClient googleOAuthClient;
    private final UserReader userReader;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public LoginResponseDto loginWithGoogle(String authorizationCode) {
        TokenResponse token = googleOAuthClient.exchangeCodeForToken(authorizationCode);
        if (token == null || token.getAccess_token() == null || token.getAccess_token().isBlank()) {
            log.warn("Google token exchange failed: {}", token);
            throw new CustomException(ErrorCode.UNAUTHORIZED_DOMAIN);
        }

        GoogleUserInfoDto userInfoDto = googleOAuthClient.getUserInfo(token.getAccess_token());
        if (userInfoDto == null || userInfoDto.getEmail() == null || userInfoDto.getEmail().isBlank()) {
            log.warn("Google userinfo missing email: {}", userInfoDto);
            throw new CustomException(ErrorCode.UNAUTHORIZED_DOMAIN);
        }

        String email = userInfoDto.getEmail().trim().toLowerCase();

//        String requiredDomain = "bssm.hs.kr";
//        if (userInfoDto.getHd() != null && !userInfoDto.getHd().equalsIgnoreCase(requiredDomain)) {
//            throw new CustomException(ErrorCode.UNAUTHORIZED_DOMAIN);
//        }
//        if (userInfoDto.getHd() == null && !email.endsWith("@" + requiredDomain)) {
//            throw new CustomException(ErrorCode.UNAUTHORIZED_DOMAIN);
//        }
//
//        if (Boolean.FALSE.equals(userInfoDto.getEmail_verified())) {
//            log.warn("Email not verified: {}", email);
//            throw new CustomException(ErrorCode.UNAUTHORIZED_DOMAIN);
//        }

        UserInfo userInfo = userReader.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_USER));

        String access = jwtProvider.createAccessToken(userInfo.getUserId(), userInfo.getUserType().name());
        String refresh = jwtProvider.createRefreshToken(userInfo.getUserId(), userInfo.getUserType());

        String redisKey = "refresh:" + userInfo.getUserType().name() + ":" + userInfo.getUserId();
        long refreshTtlMillis = jwtProvider.getRefreshTokenExpirationMillis();
        redisTemplate.opsForValue().set(redisKey, refresh, refreshTtlMillis, TimeUnit.MILLISECONDS);

        return LoginResponseDto.of(userInfo, access, refresh);
    }
}
