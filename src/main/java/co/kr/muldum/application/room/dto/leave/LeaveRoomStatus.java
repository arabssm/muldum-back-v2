package co.kr.muldum.application.room.dto.leave;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LeaveRoomStatus {
    private Long userId;
    private String roomId;
    private LocalDateTime executedAt;
}
