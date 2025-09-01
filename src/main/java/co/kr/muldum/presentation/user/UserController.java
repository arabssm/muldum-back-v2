package co.kr.muldum.presentation.user;

import co.kr.muldum.domain.user.dto.UserIssueResponseDto;
import co.kr.muldum.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/issue")
    public ResponseEntity<UserIssueResponseDto> fixUserIssues() {
        log.info("팀 ID가 null인 사용자 문제 해결 요청 접수");
        UserIssueResponseDto response = userService.fixNullTeamIds();

        return ResponseEntity.ok(response);
    }
}