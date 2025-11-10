package co.kr.muldum.infrastructure.check;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ara")
public class TestController {

    @GetMapping("/auth/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("OAuth endpoint is working!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> simpleTest() {
        return ResponseEntity.ok("Simple test endpoint working!");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("health check");
    }
}
