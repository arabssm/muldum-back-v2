package co.kr.muldum.calendar.presentation;

import co.kr.muldum.calendar.application.GoogleCalendarService;
import co.kr.muldum.calendar.application.dto.GoogleCalendarEventsResponse;
import co.kr.muldum.calendar.presentation.dto.GoogleCalendarSyncRequest;
import co.kr.muldum.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/std/calender/google")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    @GetMapping
    public ResponseEntity<GoogleCalendarEventsResponse> getGoogleCalendarEvents(
            @ModelAttribute GoogleCalendarSyncRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        GoogleCalendarEventsResponse response = googleCalendarService.getEvents(userId, request);
        return ResponseEntity.ok(response);
    }
}
