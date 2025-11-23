package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.model.NthStatus;
import co.kr.muldum.domain.item.model.NthStatusHistory;
import co.kr.muldum.domain.item.repository.NthStatusHistoryRepository;
import co.kr.muldum.domain.item.repository.NthStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class NthStatusScheduler {

    private final NthStatusRepository nthStatusRepository;
    private final NthStatusHistoryRepository nthStatusHistoryRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoCreateDailyNthStatus() {
        log.info("자동 n차 생성 스케줄러 실행 시작");

        try {
            NthStatus currentStatus = nthStatusRepository.findByNthStatusId(1L)
                    .orElseGet(() -> createInitialNthStatus());

            Integer nextNth = calculateNextNth();
            String todayDate = LocalDate.now().toString();

            currentStatus.updateNthValue(
                    nextNth,
                    "DAILY_AUTO",
                    Collections.emptyList(),
                    todayDate,
                    null
            );
            nthStatusRepository.save(currentStatus);

            saveToHistory(nextNth, todayDate);

            log.info("{}차 물품 신청 기간 자동 생성 완료 - 날짜: {}", nextNth, todayDate);
        } catch (Exception e) {
            log.error("자동 n차 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private NthStatus createInitialNthStatus() {
        log.info("초기 NthStatus 생성");
        return nthStatusRepository.save(NthStatus.builder()
                .nthValue(0)
                .build());
    }

    private Integer calculateNextNth() {
        long historyCount = nthStatusHistoryRepository.count();
        return (int) historyCount + 1;
    }

    private void saveToHistory(Integer nth, String date) {
        NthStatusHistory history = NthStatusHistory.builder()
                .nthValue(nth)
                .projectType("DAILY_AUTO")
                .guide(Collections.emptyList())
                .deadlineDate(date)
                .teacherId(null)
                .build();
        nthStatusHistoryRepository.save(history);
        log.debug("NthStatusHistory 저장 완료 - nth: {}", nth);
    }
}

