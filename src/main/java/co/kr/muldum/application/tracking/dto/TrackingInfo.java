package co.kr.muldum.application.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrackingInfo {
    private String status;
    private String location;
    private Double latitude;
    private Double longitude;
}
