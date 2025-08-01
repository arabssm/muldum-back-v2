package co.kr.muldum.presentation.user;

import co.kr.muldum.application.user.LoginResponseDto;
import co.kr.muldum.application.user.OAuthLoginService;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleLoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ara/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/login/google")
    public ResponseEntity<LoginResponseDto> loginWithGoogle(
            @RequestBody GoogleLoginRequestDto request
    ) {
        LoginResponseDto response = oAuthLoginService.loginWithGoogle(request.getAccessToken());
        return ResponseEntity.ok(response);
    }
}
