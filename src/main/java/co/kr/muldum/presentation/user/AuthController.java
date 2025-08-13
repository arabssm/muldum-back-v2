package co.kr.muldum.presentation.user;

import co.kr.muldum.application.auth.TokenRefreshService;
import co.kr.muldum.application.user.LoginResponseDto;
import co.kr.muldum.application.user.OAuthLoginService;
import co.kr.muldum.global.dto.MessageResponse;
import co.kr.muldum.global.dto.TokenRefreshRequestDto;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleLoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ara/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthLoginService oAuthLoginService;
    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/login/google")
    public ResponseEntity<LoginResponseDto> loginWithGoogle(
            @RequestBody GoogleLoginRequestDto request
    ) {
        LoginResponseDto response = oAuthLoginService.loginWithGoogle(request.getAuthorizationCode());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<MessageResponse> refreshAccessToken(@RequestBody TokenRefreshRequestDto request) {
        String newAccessToken = tokenRefreshService.refreshAccessToken(request);
        return ResponseEntity.ok(new MessageResponse(newAccessToken));
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(co.kr.muldum.global.exception.CustomException.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> handleAuthErrors(co.kr.muldum.global.exception.CustomException ex) {
        // 인증 코드 오류 → 404 with spec body
        String msg = ex.getMessage();
        if (msg != null && (msg.contains("잘못된 인증 코드") || msg.contains("인증 코드"))) {
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("statusCode", 404);
            body.put("message", "인증 코드를 찾을 수 없습니다");
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(body);
        }
        // 그 외 → 400 with spec body
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("error", "UNREGISTERED_USER");
        body.put("message", msg == null ? "등록되지 않은 사용자입니다." : msg);
        return org.springframework.http.ResponseEntity.badRequest().body(body);
    }
}
