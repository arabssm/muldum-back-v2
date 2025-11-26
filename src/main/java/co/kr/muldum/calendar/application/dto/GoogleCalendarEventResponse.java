package co.kr.muldum.calendar.application.dto;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.Builder;
import lombok.Getter;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;

@Getter
@Builder
public class GoogleCalendarEventResponse {

    private String eventId;
    private String title;
    private String description;
    private String startDateTime;
    private String endDateTime;
    private boolean allDay;
    private String status;
    private String htmlLink;
    private String organizer;
    private String location;

    public static GoogleCalendarEventResponse from(Event event) {
        if (event == null) {
            return null;
        }

        EventDateTime start = event.getStart();
        EventDateTime end = event.getEnd();

        return GoogleCalendarEventResponse.builder()
                .eventId(event.getId())
                .title(event.getSummary())
                .description(event.getDescription())
                .startDateTime(toIsoString(start))
                .endDateTime(toIsoString(end))
                .allDay(isAllDay(start, end))
                .status(event.getStatus())
                .htmlLink(event.getHtmlLink())
                .organizer(event.getOrganizer() != null
                        ? coalesce(event.getOrganizer().getDisplayName(), event.getOrganizer().getEmail())
                        : null)
                .location(event.getLocation())
                .build();
    }

    private static boolean isAllDay(EventDateTime start, EventDateTime end) {
        return (start != null && start.getDate() != null)
                || (end != null && end.getDate() != null);
    }

    private static String toIsoString(EventDateTime eventDateTime) {
        if (eventDateTime == null) {
            return null;
        }

        DateTime googleDate = eventDateTime.getDateTime() != null
                ? eventDateTime.getDateTime()
                : eventDateTime.getDate();

        if (googleDate == null) {
            return null;
        }

        ZoneId zoneId = resolveZone(eventDateTime.getTimeZone());
        Instant instant = Instant.ofEpochMilli(googleDate.getValue());
        return instant.atZone(zoneId).toOffsetDateTime().toString();
    }

    private static ZoneId resolveZone(String timeZoneId) {
        if (timeZoneId == null || timeZoneId.isBlank()) {
            return ZoneId.of("UTC");
        }
        try {
            return ZoneId.of(timeZoneId);
        } catch (DateTimeException ex) {
            return ZoneId.of("UTC");
        }
    }

    private static String coalesce(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }
}
