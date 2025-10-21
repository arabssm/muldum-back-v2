package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.TeamspaceService;
import co.kr.muldum.application.teamspace.dto.TeamspaceInviteRequestDto;
import co.kr.muldum.application.teamspace.dto.TeamspaceInviteResponseDto;
import co.kr.muldum.application.teamspace.dto.TeamspaceResponseDto;
import co.kr.muldum.application.teamspace.dto.TeamspaceWithItemResponseDto;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamspaceController {

    private final TeamspaceService teamspaceService;

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
    public List<TeamspaceWithItemResponseDto> getTeamspaceWithItem(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return teamspaceService.getTeamspaceWithItem(userDetails.getUserId());
    }

    // 전공동아리 팀 조회
    @GetMapping("/ara/teamspace/major")
    public TeamspaceResponseDto getMajorTeams() {
        return teamspaceService.getMajorTeams();
    }
}
