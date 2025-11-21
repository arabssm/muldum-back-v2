package co.kr.muldum.application.room.dto.participant;

import co.kr.muldum.domain.room.model.enums.RoomRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ParticipantResponse {
    private Long userId;
    private RoomRole role;
}
