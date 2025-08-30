package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.presentation.teamspace.dto.TeamspaceInviteRequestDto;
import co.kr.muldum.presentation.teamspace.dto.TeamspaceInviteResponse;
import co.kr.muldum.application.teamspace.TeamspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/std/team")
@RequiredArgsConstructor
public class TeamspaceController {

    private final TeamspaceService teamspaceService;

    @PostMapping("/invite")
    public ResponseEntity<TeamspaceInviteResponse> inviteStudents(
            @RequestBody TeamspaceInviteRequestDto request
    ) {
        TeamspaceInviteResponse response = teamspaceService.inviteStudents(request);
        return ResponseEntity.status(201).body(response);
    }
}
