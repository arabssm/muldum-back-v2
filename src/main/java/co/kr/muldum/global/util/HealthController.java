package co.kr.muldum.global.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/ara/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
