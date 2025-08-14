package co.kr.muldum.presentation.user;

import co.kr.muldum.application.auth.TokenRefreshService;
import co.kr.muldum.application.user.LoginResponseDto;
import co.kr.muldum.application.user.OAuthLoginService;
import co.kr.muldum.global.dto.MessageResponse;
import co.kr.muldum.global.dto.TokenRefreshRequestDto;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleLoginRequestDto;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.LinkedHashMap;
import java.util.Map;
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
        return ResponseEntity.ok(oAuthLoginService.loginWithGoogle(request.getAuthorizationCode()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<MessageResponse> refreshAccessToken(@RequestBody TokenRefreshRequestDto request) {
        String newAccessToken = tokenRefreshService.refreshAccessToken(request);
        return ResponseEntity.ok(new MessageResponse(newAccessToken));
    }
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleAuthErrors(CustomException ex) {
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
}
