package co.kr.muldum.application.calender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CalenderResponseDto {
    private Long id;
    private Long teamId;
    private int year;
    private int date;
    private String title;
    private String content;
}