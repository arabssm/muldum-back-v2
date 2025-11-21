package co.kr.muldum.application.room.dto.room.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRoomRequest {

    @NotBlank
    private String title;

    @NotNull
    private Long teamId;

    @NotNull
    @Min(1)
    private Integer maxParticipants;
}
