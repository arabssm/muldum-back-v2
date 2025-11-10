package co.kr.muldum.infrastructure.check;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogSchedule {

    @Scheduled(cron = "*/10 * * * * *")
    private void printInfoMessage() {
        log.info("일반 메세지 출력");
    }

    @Scheduled(cron = "0 * * * * *")
    private void printErrorMessage() {
        log.error("에러 메세지 출력");
    }

    @Scheduled(cron = "*/30 * * * * *")
    private void printWarnMessage() {
        log.warn("Warning 메세지 출력");
    }

}
