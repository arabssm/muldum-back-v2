package co.kr.muldum.presentation.tracking;

import co.kr.muldum.application.tracking.dto.TrackingInfo;
import co.kr.muldum.application.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/{trackingNumber}")
    public Mono<TrackingInfo> getTrackingInfo(@PathVariable String trackingNumber) {
        return trackingService.getTrackingInfo(trackingNumber);
    }
}
