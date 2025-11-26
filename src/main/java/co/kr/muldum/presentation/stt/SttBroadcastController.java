package co.kr.muldum.presentation.stt;

import co.kr.muldum.presentation.room.websocket.SignalHandler;
import co.kr.muldum.presentation.stt.dto.SttBroadcastRequest;
import co.kr.muldum.presentation.stt.dto.SttBroadcastResponse;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/stt")
@RequiredArgsConstructor
public class SttBroadcastController {

    private static final String STT_TYPE = "stt";

    private final SignalHandler signalHandler;

    @PostMapping("/broadcast")
    public ResponseEntity<SttBroadcastResponse> broadcast(
            @Valid @RequestBody SttBroadcastRequest request
    ) {
        validateType(request.type());

        Map<String, Object> message = new HashMap<>();
        message.put("type", STT_TYPE);
        message.put("roomId", request.roomId());
        message.put("userId", request.userId());
        message.put("userName", request.userName());
        message.put("transcript", request.transcript());
        message.put("timestamp", request.timestamp());

        int recipients = signalHandler.broadcastToRoom(request.roomId(), message);
        SttBroadcastResponse response = new SttBroadcastResponse(true, recipients);
        return ResponseEntity.ok(response);
    }

    private void validateType(String type) {
        if (!STT_TYPE.equalsIgnoreCase(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type must be 'stt'");
        }
    }
}
