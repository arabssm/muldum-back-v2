package co.kr.muldum.pressentation.user;

import co.kr.muldum.application.user.LoginResponse;
import co.kr.muldum.application.user.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ara/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/login/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(
            @RequestBody GoogleLoginRequest request
    ) {
        LoginResponse response = oAuthLoginService.loginWithGoogle(request.getAccessToken());
        return ResponseEntity.ok(response);
    }
}
