package co.kr.muldum.application.room.dto.room.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RoomSummaryResponse {
    private String roomId;
    private String title;
    private Long teamId;
    private String createdBy;
    private LocalDateTime createdAt;
    private Integer participants;
    private Integer maxParticipants;
    private String status;
}
