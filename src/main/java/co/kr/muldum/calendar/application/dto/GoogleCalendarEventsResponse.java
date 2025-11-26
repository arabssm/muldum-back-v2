package co.kr.muldum.calendar.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GoogleCalendarEventsResponse {

    private String calendarId;
    private List<GoogleCalendarEventResponse> events;
    private String nextPageToken;
    private String nextSyncToken;
}
