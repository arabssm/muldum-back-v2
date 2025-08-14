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
public class GoogleOAuthController {

    private final OAuthLoginService oAuthLoginService;
    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/login/google")
    public ResponseEntity<LoginResponseDto> loginWithGoogle(
            @RequestBody GoogleLoginRequestDto request
    ) {
        LoginResponseDto response = oAuthLoginService.loginWithGoogle(request.getAccessToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<MessageResponse> refreshAccessToken(@RequestBody TokenRefreshRequestDto request) {
        String newAccessToken = tokenRefreshService.refreshAccessToken(request);
        return ResponseEntity.ok(new MessageResponse(newAccessToken));
    }
}
