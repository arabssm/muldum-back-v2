package co.kr.muldum.calendar.application.dto;

import co.kr.muldum.calendar.domain.StudentCalendar;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentCalendarResponse {

    @JsonProperty("calender_id")
    private Long calendarId;

    @JsonProperty("Startyear")
    private int startYear;

    @JsonProperty("Startdate")
    private int startDate;

    @JsonProperty("Endyear")
    private int endYear;

    @JsonProperty("Enddate")
    private int endDate;

    private String title;
    private String content;

    public static StudentCalendarResponse from(StudentCalendar calendar) {
        return StudentCalendarResponse.builder()
                .calendarId(calendar.getId())
                .startYear(calendar.getStartDate().getYear())
                .startDate(calendar.getStartDate().getMonthValue() * 100 + calendar.getStartDate().getDayOfMonth())
                .endYear(calendar.getEndDate().getYear())
                .endDate(calendar.getEndDate().getMonthValue() * 100 + calendar.getEndDate().getDayOfMonth())
                .title(calendar.getTitle())
                .content(calendar.getContent())
                .build();
    }
}
