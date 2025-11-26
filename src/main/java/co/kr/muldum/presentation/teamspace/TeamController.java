package co.kr.muldum.presentation.teamspace;

import co.kr.muldum.application.teamspace.TeamService;
import co.kr.muldum.application.teamspace.dto.TeamPageUpdateRequest;
import co.kr.muldum.calendar.application.CalendarTeamResolver;
import co.kr.muldum.global.dto.MessageResponse;
import co.kr.muldum.global.security.CustomUserDetails;
import co.kr.muldum.global.util.SecurityUtil;
import co.kr.muldum.presentation.teamspace.dto.TeamGoogleCalendarRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/std/teamspace/network/team")
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final TeamService teamService;
    private final CalendarTeamResolver calendarTeamResolver;

    @PatchMapping("/{team_id}")
    public ResponseEntity<MessageResponse> updateTeamPage(
            @PathVariable("team_id") Long teamId,
            @RequestBody @Valid TeamPageUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        log.info("TeamController 요청 수신 - teamId: {}, userId: {} name: {}, content: {}", teamId, customUserDetails != null ? customUserDetails.getUserId() : "null", request.name(), request.content());
        teamService.updateTeamPage(teamId, request.name(), request.content(), customUserDetails.getUserId());
        return ResponseEntity.ok(new MessageResponse("팀 페이지가 성공적으로 수정되었습니다."));
    }

    @PatchMapping("/google-calendar")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MessageResponse> linkGoogleCalendar(
            @Valid @RequestBody TeamGoogleCalendarRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long teamId = calendarTeamResolver.resolveTeamId(userId);
        teamService.updateTeamGoogleCalendar(teamId, request.getGoogleCalendarId().trim(), userId);
        return ResponseEntity.ok(new MessageResponse("구글 캘린더 ID가 저장되었습니다."));
    }
}
