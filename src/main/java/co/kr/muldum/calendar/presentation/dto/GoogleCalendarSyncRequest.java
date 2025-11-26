package co.kr.muldum.calendar.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleCalendarSyncRequest {

    private String calendarId;
    private String timeMin;
    private String timeMax;
    private Integer maxResults;
    private String pageToken;
    private String timeZone;
}
