package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.TeamspaceService;
import co.kr.muldum.application.teamspace.dto.TeamspaceInviteRequestDto;
import co.kr.muldum.application.teamspace.dto.TeamspaceInviteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/std/team")
@RequiredArgsConstructor
public class TeamspaceController {

    private final TeamspaceService teamspaceService;

    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamspaceInviteResponseDto inviteStudents(
            @RequestBody TeamspaceInviteRequestDto requestDto) {
        return teamspaceService.inviteStudents(requestDto);
    }
}