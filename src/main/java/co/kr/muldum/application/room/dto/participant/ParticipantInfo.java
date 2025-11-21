package co.kr.muldum.application.room.dto.participant;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ParticipantInfo {
    private int amount;
    private List<ParticipantResponse> list;
}
