package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.TeamspaceService;
import co.kr.muldum.application.teamspace.dto.*;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TeamspaceController {

    private final TeamspaceService teamspaceService;

    @DeleteMapping("/tch/teamspace/network/team/{team_id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteTeam(
            @PathVariable("team_id") Long teamId
    ) {
        try {
            teamspaceService.deleteTeam(teamId);
            return ResponseEntity.ok(new DeleteTeamResponse(teamId, "팀이 성공적으로 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/tch/teamspace/invite")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamspaceInviteResponseDto inviteStudents(
            @RequestBody TeamspaceInviteRequestDto requestDto) {
        return teamspaceService.inviteStudents(requestDto);
    }

    @GetMapping("/ara/teamspace/network")
    public TeamspaceResponseDto getTeamspace() {
        return teamspaceService.getTeamspace();
    }

    @GetMapping("/tch/teamspace/network/item")
    @PreAuthorize("hasRole('TEACHER')")
    public TeamspaceWithItemResponseDto getTeamspaceWithItem(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return teamspaceService.getTeamspaceWithItem(userDetails.getUserId());
    }

    // 전공동아리 팀 조회
    @GetMapping("/ara/teamspace/major")
    public TeamspaceResponseDto getMajorTeams() {
        return teamspaceService.getMajorTeams();
    }
}
