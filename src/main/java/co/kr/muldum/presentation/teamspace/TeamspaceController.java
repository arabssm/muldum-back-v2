package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.StudentCsvImportRequest;
import co.kr.muldum.application.teamspace.TeamspaceService;
import co.kr.muldum.presentation.teamspace.dto.TeamspaceInviteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/std/team")
@RequiredArgsConstructor
public class TeamspaceController {

    private final TeamspaceService teamspaceService;

    @PostMapping("/invite")
    public ResponseEntity<TeamspaceInviteResponseDto> inviteStudents(
            @RequestBody StudentCsvImportRequest request
    ) {
        TeamspaceInviteResponseDto response = teamspaceService.inviteStudents(request);
        return ResponseEntity.status(201).body(response);
    }
}
