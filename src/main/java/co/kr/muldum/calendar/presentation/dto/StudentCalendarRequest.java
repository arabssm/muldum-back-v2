package co.kr.muldum.calendar.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCalendarRequest {

    @JsonProperty("Startyear")
    private Integer startYear;

    @JsonProperty("Startdate")
    private Integer startDate;

    @JsonProperty("Endyear")
    private Integer endYear;

    @JsonProperty("Enddate")
    private Integer endDate;

    private String title;
    private String content;
}
