package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.TeamPageQueryService;
import co.kr.muldum.application.teamspace.dto.TeamPageQueryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ara/teamspace/network/team")
public class TeamPageController {

    private final TeamPageQueryService teamPageQueryService;

    @GetMapping("/{team_id}")
    public ResponseEntity<TeamPageQueryResponseDto> getTeamPage(
            @PathVariable("team_id") Long teamId
    ) {
        return ResponseEntity.ok(teamPageQueryService.getTeamPage(teamId));
    }
}
