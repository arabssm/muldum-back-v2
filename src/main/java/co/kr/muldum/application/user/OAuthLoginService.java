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
        try {
            log.info("[OAuth] Starting Google login process with code: {}", 
                authorizationCode != null ? "present" : "null");
            
            // 1. Google에서 토큰 가져오기
            TokenResponse token = googleOAuthClient.exchangeCodeForToken(authorizationCode);
            if (token == null || token.getAccess_token() == null || token.getAccess_token().isBlank()) {
                log.error("[OAuth] Failed to get token from Google. Token: {}", token);
                throw new CustomException(ErrorCode.LOGIN_FAILED);
            }
            log.info("[OAuth] Successfully got token from Google");

            // 2. Google에서 사용자 정보 가져오기
            GoogleUserInfoDto userInfoDto = googleOAuthClient.getUserInfo(token.getAccess_token());
            if (userInfoDto == null || userInfoDto.getEmail() == null || userInfoDto.getEmail().isBlank()) {
                log.error("[OAuth] Failed to get user info from Google. UserInfo: {}", userInfoDto);
                throw new CustomException(ErrorCode.LOGIN_FAILED);
            }
            
            String email = userInfoDto.getEmail().trim().toLowerCase();
            log.info("[OAuth] Got user info - email: {}, verified: {}, domain: {}", 
                email, userInfoDto.getEmail_verified(), userInfoDto.getHd());

            // 3. DB에서 사용자 찾기 (없으면 에러)
            UserInfo userInfo = userReader.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[OAuth] User not found in database: {}", email);
                    return new CustomException(ErrorCode.UNREGISTERED_USER);
                });
            
            log.info("[OAuth] User found - ID: {}, Type: {}, Name: {}", 
                userInfo.getUserId(), userInfo.getUserType(), userInfo.getName());

            // 4. JWT 토큰 생성
            String accessToken = jwtProvider.createAccessToken(userInfo.getUserId(), userInfo.getUserType().name());
            String refreshToken = jwtProvider.createRefreshToken(userInfo.getUserId(), userInfo.getUserType());
            
            log.info("[OAuth] Generated JWT tokens for user: {}", userInfo.getUserId());

            // 5. Redis에 Refresh Token 저장
            String redisKey = "refresh:" + userInfo.getUserType().name() + ":" + userInfo.getUserId();
            long refreshTtlMillis = jwtProvider.getRefreshTokenExpirationMillis();
            redisTemplate.opsForValue().set(redisKey, refreshToken, refreshTtlMillis, TimeUnit.MILLISECONDS);
            
            log.info("[OAuth] Saved refresh token to Redis: {}", redisKey);

            // 6. 로그인 응답 생성
            LoginResponseDto response = LoginResponseDto.of(userInfo, accessToken, refreshToken);
            log.info("[OAuth] Login successful for user: {}", email);
            
            return response;
            
        } catch (CustomException e) {
            log.error("[OAuth] Custom exception during login: {} - {}", e.getMessage(), e.getClass().getSimpleName());
            throw e;
        } catch (Exception e) {
            log.error("[OAuth] Unexpected error during login", e);
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
    }
}
