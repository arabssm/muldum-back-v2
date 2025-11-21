package co.kr.muldum.application.room.dto.leave;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LeaveRoomResponse {
    private String message;
    private LeaveRoomStatus status;
}
