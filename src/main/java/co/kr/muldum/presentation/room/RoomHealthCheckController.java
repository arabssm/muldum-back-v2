package co.kr.muldum.presentation.room;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomHealthCheckController {

    @GetMapping("/ara/rooms/health")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
