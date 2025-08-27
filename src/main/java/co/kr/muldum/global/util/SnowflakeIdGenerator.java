package co.kr.muldum.global.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class  SnowflakeIdGenerator {

    private static final long EPOCH = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
            .atZone(ZoneId.of("Asia/Seoul"))
            .toInstant()
            .toEpochMilli() / 1000 / 60;

    private static final int TEAM_ID_BITS = 12;
    private static final int SEQUENCE_BITS = 7;

    private static final long MAX_TEAM_ID = (1L << TEAM_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public synchronized long generateId(int teamId) {
        if (teamId > MAX_TEAM_ID) {
            throw new IllegalArgumentException("Team ID cannot be greater than " + MAX_TEAM_ID);
        }

        long timestamp = getCurrentTimestamp();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = waitForNextMinute(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return (timestamp << (TEAM_ID_BITS + SEQUENCE_BITS))
                | ((long) teamId << SEQUENCE_BITS)
                | sequence;
    }

    private long getCurrentTimestamp() {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli() / 1000 / 60 - EPOCH;
    }

    private long waitForNextMinute(long lastTimestamp) {
        long nextMinute = lastTimestamp + 1;
        
        while (true) {
            long now = getCurrentTimestamp();
            if (now >= nextMinute) {
                return now;
            }
            
            // 다음 분까지 남은 시간을 계산 (밀리초)
            long currentTimeMillis = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
            long nextMinuteMillis = (nextMinute + EPOCH) * 60 * 1000;
            long sleepTime = nextMinuteMillis - currentTimeMillis;
            
            // 최소 1ms, 최대 1000ms로 제한
            sleepTime = Math.max(1, Math.min(sleepTime, 1000));
            
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for next minute", ie);
            }
        }
    }
}