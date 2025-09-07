package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.TeamService;
import co.kr.muldum.application.teamspace.dto.TeamPageUpdateRequest;
import co.kr.muldum.application.teamspace.dto.TeamPageUpdateResponse;
import co.kr.muldum.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/std/teamspace/network/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PatchMapping("/{team_id}")
    public ResponseEntity<TeamPageUpdateResponse> updateTeamPage(
            @PathVariable("team_id") Long teamId,
            @RequestBody @Valid TeamPageUpdateRequest request
    ) {
        Long currentUserId = getCurrentUserId();
        teamService.updateTeamPage(teamId, request.content(), currentUserId);
        return ResponseEntity.ok(new TeamPageUpdateResponse("팀 페이지가 성공적으로 수정되었습니다."));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return 0L;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUserId();
        }
        return 0L;
    }
}
