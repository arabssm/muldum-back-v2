package co.kr.muldum.infrastructure.check;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogSchedule {
    @Scheduled(fixedRate = 600000)
    public void logEveryMinute() {
        log.info("Scheduled task executed: Logging every 10 minute.");
    }
}
