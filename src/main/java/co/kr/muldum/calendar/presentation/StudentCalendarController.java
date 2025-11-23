package co.kr.muldum.calendar.presentation;

import co.kr.muldum.calendar.application.StudentCalendarService;
import co.kr.muldum.calendar.application.dto.StudentCalendarResponse;
import co.kr.muldum.calendar.presentation.dto.StudentCalendarRequest;
import co.kr.muldum.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/std/calender")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentCalendarController {

    private final StudentCalendarService studentCalendarService;

    @GetMapping
    public ResponseEntity<List<StudentCalendarResponse>> getCalendars() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<StudentCalendarResponse> calendars = studentCalendarService.getCalendars(userId);
        return ResponseEntity.ok(calendars);
    }

    @PostMapping
    public ResponseEntity<StudentCalendarResponse> createCalendar(
            @RequestBody StudentCalendarRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        StudentCalendarResponse response = studentCalendarService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{calendarId}")
    public ResponseEntity<StudentCalendarResponse> updateCalendar(
            @PathVariable("calendarId") Long calendarId,
            @RequestBody StudentCalendarRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        StudentCalendarResponse response = studentCalendarService.update(userId, calendarId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{calendarId}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable("calendarId") Long calendarId) {
        Long userId = SecurityUtil.getCurrentUserId();
        studentCalendarService.delete(userId, calendarId);
        return ResponseEntity.noContent().build();
    }
}
