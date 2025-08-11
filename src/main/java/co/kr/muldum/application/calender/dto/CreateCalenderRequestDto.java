package co.kr.muldum.application.calender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCalenderRequestDto {
    private Integer year;   // 예: 2025
    private Integer date;   // 예: 1219 (MMdd)
    private String title;   // 예: "김현우 생일"
    private String content; // 예: "왜이렇게사니"
}