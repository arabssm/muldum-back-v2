package co.kr.muldum.presentation.user;

import co.kr.muldum.application.user.LogoutService;
import co.kr.muldum.global.dto.MessageResponse;
import co.kr.muldum.presentation.dto.LogoutRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ara/auth")
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        logoutService.logout(logoutRequestDto);
        return ResponseEntity.ok(new MessageResponse("로그아웃 되었습니다."));
    }
}
