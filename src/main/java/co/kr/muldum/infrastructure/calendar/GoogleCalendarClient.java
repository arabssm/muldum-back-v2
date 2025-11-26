package co.kr.muldum.infrastructure.calendar;

import co.kr.muldum.calendar.application.dto.GoogleCalendarEventResponse;
import co.kr.muldum.calendar.application.dto.GoogleCalendarEventsResponse;
import co.kr.muldum.calendar.domain.StudentCalendar;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleCalendarClient {

    private static final int DEFAULT_MAX_RESULTS = 50;
    private static final int MAX_RESULTS_LIMIT = 2500;

    private final Calendar calendar;

    @Value("${google.calendar.default-calendar-id:}")
    private String defaultCalendarId;

    @Value("${google.calendar.time-zone:Asia/Seoul}")
    private String defaultTimeZone;

    public GoogleCalendarEventsResponse listEvents(
            String calendarId,
            Instant timeMin,
            Instant timeMax,
            Integer maxResults,
            String pageToken,
            ZoneId zoneId
    ) {
        String targetCalendarId = resolveCalendarId(calendarId);
        ZoneId resolvedZone = resolveZone(zoneId);
        int pageSize = resolvePageSize(maxResults);

        try {
            Calendar.Events.List request = calendar.events()
                    .list(targetCalendarId)
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .setMaxResults(pageSize)
                    .setPageToken(pageToken)
                    .setTimeZone(resolvedZone.getId());

            if (timeMin != null) {
                request.setTimeMin(toGoogleDateTime(timeMin, resolvedZone));
            }
            if (timeMax != null) {
                request.setTimeMax(toGoogleDateTime(timeMax, resolvedZone));
            }

            Events events = request.execute();
            List<GoogleCalendarEventResponse> mapped = (events.getItems() == null ? List.<com.google.api.services.calendar.model.Event>of() : events.getItems())
                    .stream()
                    .map(GoogleCalendarEventResponse::from)
                    .filter(java.util.Objects::nonNull)
                    .toList();

            return GoogleCalendarEventsResponse.builder()
                    .calendarId(targetCalendarId)
                    .events(mapped)
                    .nextPageToken(events.getNextPageToken())
                    .nextSyncToken(events.getNextSyncToken())
                    .build();
        } catch (GoogleJsonResponseException e) {
            log.error("Google Calendar API error: status={}, message={}, calendarId={}",
                    e.getStatusCode(), e.getMessage(), targetCalendarId, e);
            throw handleGoogleApiException(e, targetCalendarId);
        } catch (IOException e) {
            log.error("Failed to fetch events from Google Calendar calendarId={}", targetCalendarId, e);
            throw new CustomException(ErrorCode.GOOGLE_CALENDAR_SYNC_FAILED);
        }
    }

    public ZoneId getDefaultZoneId() {
        return resolveZone(null);
    }

    public String getDefaultCalendarId() {
        return defaultCalendarId;
    }

    public String createEvent(String calendarId, StudentCalendar studentCalendar, ZoneId zoneId) {
        String targetCalendarId = resolveCalendarId(calendarId);
        Event event = toEvent(studentCalendar, zoneId);
        try {
            Event created = calendar.events()
                    .insert(targetCalendarId, event)
                    .setSendNotifications(false)
                    .execute();
            return created.getId();
        } catch (GoogleJsonResponseException e) {
            log.error("Google Calendar API error during create: status={}, calendarId={}",
                    e.getStatusCode(), targetCalendarId, e);
            throw handleGoogleApiException(e, targetCalendarId);
        } catch (IOException e) {
            log.error("Failed to create event on Google Calendar calendarId={}", targetCalendarId, e);
            throw new CustomException(ErrorCode.GOOGLE_CALENDAR_SYNC_FAILED);
        }
    }

    public void updateEvent(String calendarId, String eventId, StudentCalendar studentCalendar, ZoneId zoneId) {
        if (eventId == null || eventId.isBlank()) {
            throw new CustomException(ErrorCode.GOOGLE_CALENDAR_SYNC_FAILED, "Google event ID가 없습니다.");
        }
        String targetCalendarId = resolveCalendarId(calendarId);
        Event event = toEvent(studentCalendar, zoneId);
        try {
            calendar.events()
                    .update(targetCalendarId, eventId, event)
                    .setSendNotifications(false)
                    .execute();
        } catch (GoogleJsonResponseException e) {
            log.error("Google Calendar API error during update: status={}, eventId={}, calendarId={}",
                    e.getStatusCode(), eventId, targetCalendarId, e);
            throw handleGoogleApiException(e, targetCalendarId);
        } catch (IOException e) {
            log.error("Failed to update Google Calendar event={} calendarId={}", eventId, targetCalendarId, e);
            throw new CustomException(ErrorCode.GOOGLE_CALENDAR_SYNC_FAILED);
        }
    }

    public void deleteEvent(String calendarId, String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return;
        }
        String targetCalendarId = resolveCalendarId(calendarId);
        try {
            calendar.events()
                    .delete(targetCalendarId, eventId)
                    .execute();
        } catch (GoogleJsonResponseException e) {
            log.error("Google Calendar API error during delete: status={}, eventId={}, calendarId={}",
                    e.getStatusCode(), eventId, targetCalendarId, e);
            throw handleGoogleApiException(e, targetCalendarId);
        } catch (IOException e) {
            log.error("Failed to delete Google Calendar event={} calendarId={}", eventId, targetCalendarId, e);
            throw new CustomException(ErrorCode.GOOGLE_CALENDAR_SYNC_FAILED);
        }
    }

    private String resolveCalendarId(String calendarId) {
        String target = (calendarId != null && !calendarId.isBlank()) ? calendarId : defaultCalendarId;
        if (target == null || target.isBlank()) {
            throw new CustomException(ErrorCode.GOOGLE_CALENDAR_NOT_CONFIGURED);
        }
        return target;
    }

    private ZoneId resolveZone(ZoneId zoneId) {
        if (zoneId != null) {
            return zoneId;
        }
        try {
            return ZoneId.of(defaultTimeZone);
        } catch (DateTimeException ex) {
            log.warn("Invalid time zone configured for Google Calendar: {}. Falling back to UTC.", defaultTimeZone);
            return ZoneId.of("UTC");
        }
    }

    private int resolvePageSize(Integer maxResults) {
        if (maxResults == null || maxResults <= 0) {
            return DEFAULT_MAX_RESULTS;
        }
        return Math.min(maxResults, MAX_RESULTS_LIMIT);
    }

    private DateTime toGoogleDateTime(Instant instant, ZoneId zoneId) {
        if (instant == null) {
            return null;
        }
        return new DateTime(Date.from(instant), TimeZone.getTimeZone(zoneId));
    }

    private Event toEvent(StudentCalendar studentCalendar, ZoneId zoneId) {
        EventDateTime start = toEventDateTime(studentCalendar.getStartDate(), false, zoneId);
        EventDateTime end = toEventDateTime(studentCalendar.getEndDate(), true, zoneId);
        Event event = new Event()
                .setSummary(studentCalendar.getTitle())
                .setDescription(studentCalendar.getContent())
                .setStart(start)
                .setEnd(end);
        return event;
    }

    private EventDateTime toEventDateTime(LocalDate date, boolean exclusiveEnd, ZoneId zoneId) {
        LocalDate targetDate = exclusiveEnd ? date.plusDays(1) : date;
        EventDateTime eventDateTime = new EventDateTime();
        eventDateTime.setDate(new DateTime(targetDate.toString()));
        eventDateTime.setTimeZone(zoneId.getId());
        return eventDateTime;
    }

    private CustomException handleGoogleApiException(GoogleJsonResponseException e, String calendarId) {
        int statusCode = e.getStatusCode();
        String message = e.getMessage();

        // 403 Forbidden - API가 비활성화되었거나 권한 없음
        if (statusCode == 403) {
            if (message != null && (message.contains("SERVICE_DISABLED") ||
                                   message.contains("has not been used") ||
                                   message.contains("accessNotConfigured"))) {
                return new CustomException(ErrorCode.GOOGLE_CALENDAR_API_DISABLED,
                        "Google Calendar API가 활성화되지 않았습니다. " +
                        "Google Cloud Console에서 Calendar API를 활성화하고 몇 분 후 재시도해주세요.");
            }
            return new CustomException(ErrorCode.GOOGLE_CALENDAR_PERMISSION_DENIED,
                    "구글 캘린더 '" + calendarId + "'에 대한 접근 권한이 없습니다.");
        }

        // 404 Not Found - 캘린더를 찾을 수 없음
        if (statusCode == 404) {
            return new CustomException(ErrorCode.GOOGLE_CALENDAR_NOT_CONFIGURED,
                    "구글 캘린더 '" + calendarId + "'를 찾을 수 없습니다.");
        }

        // 기타 에러
        return new CustomException(ErrorCode.GOOGLE_CALENDAR_SYNC_FAILED,
                "구글 캘린더 동기화 중 오류가 발생했습니다: " + message);
    }
}
