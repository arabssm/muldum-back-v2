package co.kr.muldum.presentation.user;

import co.kr.muldum.domain.user.dto.UserIssueResponseDto;
import co.kr.muldum.domain.user.service.UserService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import co.kr.muldum.domain.user.dto.UserResponseDto;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/issue")
    public ResponseEntity<String> fixUserIssues() {
        log.info("팀 ID가 null인 사용자 문제 해결 요청 접수");
        UserIssueResponseDto teamIdResponse = userService.fixNullTeamIds();

        log.info("profile이 null인 사용자 문제 해결 요청 접수");
        UserIssueResponseDto profileResponse = userService.fixNullProfiles();

        String response = String.format(
                "문제 해결 완료:\n%s, %s\n%s, %s",
                teamIdResponse.getMessage(),
                teamIdResponse.getUpdatedCount(),
                profileResponse.getMessage(),
                profileResponse.getUpdatedCount()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("현재 사용자 정보 요청 - userId: {}", userDetails.getUserId());
        UserResponseDto response = userService.getUserById(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}