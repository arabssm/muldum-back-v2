package co.kr.muldum.calendar.application;

import co.kr.muldum.calendar.application.GoogleCalendarService;
import co.kr.muldum.calendar.application.dto.GoogleCalendarEventResponse;
import co.kr.muldum.calendar.application.dto.GoogleCalendarEventsResponse;
import co.kr.muldum.calendar.application.dto.StudentCalendarResponse;
import co.kr.muldum.calendar.presentation.dto.GoogleCalendarSyncRequest;
import co.kr.muldum.calendar.presentation.dto.StudentCalendarRequest;
import co.kr.muldum.calendar.domain.StudentCalendar;
import co.kr.muldum.calendar.domain.StudentCalendarRepository;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.calendar.GoogleCalendarClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentCalendarService {

    private final StudentCalendarRepository studentCalendarRepository;
    private final CalendarTeamResolver calendarTeamResolver;
    private final GoogleCalendarClient googleCalendarClient;
    private final TeamRepository teamRepository;
    private final GoogleCalendarService googleCalendarService;

    @Transactional(readOnly = true)
    public List<StudentCalendarResponse> getCalendars(Long userId) {
        Long teamId = resolveTeamId(userId);
        List<StudentCalendar> calendars = studentCalendarRepository.findAllByTeamIdOrderByStartDateAsc(String.valueOf(teamId));
        List<StudentCalendarResponse> responses = new ArrayList<>(calendars.size());
        Map<String, Integer> googleEventIndex = new HashMap<>();
        for (int i = 0; i < calendars.size(); i++) {
            StudentCalendar calendar = calendars.get(i);
            StudentCalendarResponse response = StudentCalendarResponse.from(calendar);
            responses.add(response);
            String googleEventId = calendar.getGoogleEventId();
            if (StringUtils.hasText(googleEventId)) {
                googleEventIndex.put(googleEventId, i);
            }
        }

        Optional<String> googleCalendarId = findCalendarId(teamId);
        if (googleCalendarId.isPresent() && !googleEventIndex.isEmpty()) {
            GoogleCalendarEventsResponse googleEvents = googleCalendarService.getEvents(userId, new GoogleCalendarSyncRequest());
            if (googleEvents != null && googleEvents.getEvents() != null) {
                for (GoogleCalendarEventResponse event : googleEvents.getEvents()) {
                    Integer index = googleEventIndex.get(event.getEventId());
                    if (index != null) {
                        responses.set(index, overlayWithGoogleEvent(responses.get(index), event));
                    }
                }
            }
        }

        return responses;
    }

    public StudentCalendarResponse create(Long userId, StudentCalendarRequest request) {
        Long teamId = resolveTeamId(userId);
        LocalDate startDate = toDate(request.getStartYear(), request.getStartDate(), "Start");
        LocalDate endDate = toDate(request.getEndYear(), request.getEndDate(), "End");
        ZoneId zoneId = googleCalendarClient.getDefaultZoneId();

        StudentCalendar calendar = StudentCalendar.create(
                String.valueOf(teamId),
                String.valueOf(userId),
                startDate,
                endDate,
                request.getTitle(),
                request.getContent()
        );

        findCalendarId(teamId).ifPresent(googleCalendarId -> {
            String googleEventId = googleCalendarClient.createEvent(googleCalendarId, calendar, zoneId);
            calendar.linkGoogleEvent(googleEventId);
        });

        StudentCalendar saved = studentCalendarRepository.save(calendar);
        return StudentCalendarResponse.from(saved);
    }

    public StudentCalendarResponse update(Long userId, Long calendarId, StudentCalendarRequest request) {
        Long teamId = resolveTeamId(userId);
        StudentCalendar calendar = studentCalendarRepository.findByIdAndTeamId(calendarId, String.valueOf(teamId))
                .orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_ENTRY_NOT_FOUND));

        LocalDate startDate = toDate(request.getStartYear(), request.getStartDate(), "Start");
        LocalDate endDate = toDate(request.getEndYear(), request.getEndDate(), "End");
        calendar.update(startDate, endDate, request.getTitle(), request.getContent());
        ZoneId zoneId = googleCalendarClient.getDefaultZoneId();
        findCalendarId(teamId).ifPresent(googleCalendarId -> {
            if (calendar.getGoogleEventId() == null) {
                String eventId = googleCalendarClient.createEvent(googleCalendarId, calendar, zoneId);
                calendar.linkGoogleEvent(eventId);
            } else {
                googleCalendarClient.updateEvent(googleCalendarId, calendar.getGoogleEventId(), calendar, zoneId);
            }
        });

        return StudentCalendarResponse.from(calendar);
    }

    public void delete(Long userId, Long calendarId) {
        Long teamId = resolveTeamId(userId);
        StudentCalendar calendar = studentCalendarRepository.findByIdAndTeamId(calendarId, String.valueOf(teamId))
                .orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_ENTRY_NOT_FOUND));

        if (calendar.getGoogleEventId() != null) {
            findCalendarId(teamId).ifPresent(googleCalendarId -> googleCalendarClient.deleteEvent(googleCalendarId, calendar.getGoogleEventId()));
        }

        studentCalendarRepository.delete(calendar);
    }

    private Long resolveTeamId(Long userId) {
        return calendarTeamResolver.resolveTeamId(userId);
    }

    private Optional<String> findCalendarId(Long teamId) {
        return teamRepository.findById(teamId)
                .map(Team::getGoogleCalendarId)
                .filter(StringUtils::hasText);
    }

    private LocalDate toDate(Integer year, Integer monthDay, String prefix) {
        if (year == null || monthDay == null) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, prefix + " 날짜 정보가 필요합니다.");
        }
        if (monthDay < 101 || monthDay > 1231) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, prefix + " 날짜 형식이 올바르지 않습니다.");
        }

        String padded = String.format("%04d", monthDay);
        int month = Integer.parseInt(padded.substring(0, 2));
        int day = Integer.parseInt(padded.substring(2));

        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, prefix + " 날짜가 올바르지 않습니다.");
        }
    }

    private StudentCalendarResponse overlayWithGoogleEvent(StudentCalendarResponse base, GoogleCalendarEventResponse event) {
        if (base == null || event == null) {
            return base;
        }
        LocalDate startDate = parseIsoDate(event.getStartDateTime());
        LocalDate endDate = parseIsoDate(event.getEndDateTime());
        if (startDate == null || endDate == null) {
            return base;
        }

        // Google Calendar의 all-day 이벤트는 종료일이 exclusive이므로, 우리 시스템에 맞게 inclusive로 변경
        if (event.isAllDay() && endDate.isAfter(startDate)) {
            endDate = endDate.minusDays(1);
        }

        return StudentCalendarResponse.builder()
                .calendarId(base.getCalendarId())
                .startYear(startDate.getYear())
                .startDate(startDate.getMonthValue() * 100 + startDate.getDayOfMonth())
                .endYear(endDate.getYear())
                .endDate(endDate.getMonthValue() * 100 + endDate.getDayOfMonth())
                .title(event.getTitle() != null ? event.getTitle() : base.getTitle())
                .content(event.getDescription() != null ? event.getDescription() : base.getContent())
                .build();
    }

    private LocalDate parseIsoDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value).toLocalDate();
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        return null;
    }
}
