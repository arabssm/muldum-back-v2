package co.kr.muldum.application.room.dto.room.response;

import co.kr.muldum.application.room.dto.participant.ParticipantInfo;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RoomDetailResponse {
    private String roomId;
    private String title;
    private Long teamId;
    private String createdBy;
    private LocalDateTime createdAt;
    private ParticipantInfo participants;
    private Integer maxParticipants;
    private String status;
}
