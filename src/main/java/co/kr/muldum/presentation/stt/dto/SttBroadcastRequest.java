package co.kr.muldum.presentation.stt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SttBroadcastRequest(
        @NotBlank
        @JsonProperty("type")
        String type,

        @NotBlank
        @JsonProperty("roomId")
        String roomId,

        @NotNull
        @JsonProperty("userId")
        Long userId,

        @NotBlank
        @JsonProperty("userName")
        String userName,

        @NotBlank
        @JsonProperty("transcript")
        String transcript,

        @NotNull
        @JsonProperty("timestamp")
        Long timestamp
) {
}
