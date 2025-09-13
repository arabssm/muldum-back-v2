package co.kr.muldum.presentation.user;

import jakarta.validation.Valid;
import co.kr.muldum.application.auth.RefreshResponse;
import co.kr.muldum.application.auth.TokenRefreshService;
import co.kr.muldum.application.user.LoginResponseDto;
import co.kr.muldum.application.user.LogoutService;
import co.kr.muldum.application.user.OAuthLoginService;
import co.kr.muldum.global.dto.TokenRefreshRequestDto;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleLoginRequestDto;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.presentation.dto.LogoutRequestDto;
import co.kr.muldum.presentation.dto.LogoutResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ara/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthLoginService oAuthLoginService;
    private final TokenRefreshService tokenRefreshService;
    private final LogoutService logoutService;

    @PostMapping("/login/google")
    public ResponseEntity<LoginResponseDto> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequestDto request
    ) {
        log.info("=== Google 로그인 시도 시작 ===");
        log.info("Authorization Code: {}", request.getAuthorizationCode());
        
        try {
            LoginResponseDto response = oAuthLoginService.loginWithGoogle(request.getAuthorizationCode());
            log.info("=== Google 로그인 성공 ===");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("=== Google 로그인 실패: {} ===", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshAccessToken(@RequestBody TokenRefreshRequestDto request) {
        log.info("=== 토큰 리프레시 시도 ===");
        return ResponseEntity.ok(tokenRefreshService.refreshToken(request));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleAuthErrors(CustomException ex) {
        log.error("=== CustomException 발생: {} - {} ===", ex.getErrorCode(), ex.getMessage());
        
        ErrorCode error = ex.getErrorCode();
        HttpStatus status = error.getStatus();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error.name());
        
        if (error == ErrorCode.INVALID_AUTH_CODE) {
            status = HttpStatus.NOT_FOUND;
            body.put("message", "인증 코드를 찾을 수 없습니다");
        } else {
            body.put("message", ex.getMessage());
        }
        
        return ResponseEntity.status(status).body(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logout(
            @Valid @RequestBody LogoutRequestDto logoutRequestDto
    ) {
        logoutService.logout(logoutRequestDto);
        return ResponseEntity.ok(new LogoutResponseDto("로그아웃 되었습니다."));
    }
}
