package co.kr.muldum.global.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.locks.LockSupport;

@Component
public class  SnowflakeIdGenerator {

    private static final long EPOCH = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
            .atZone(ZoneId.of("Asia/Seoul"))
            .toInstant()
            .toEpochMilli() / 1000 / 60;

    private static final int TEAM_ID_BITS = 12;
    private static final int SEQUENCE_BITS = 10;

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
        long currentTimeMillis = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        long nextMinuteMillis = (nextMinute + EPOCH) * 60 * 1000;
        long waitTimeNanos = (nextMinuteMillis - currentTimeMillis) * 1_000_000;
        
        if (waitTimeNanos > 0) {
            LockSupport.parkNanos(waitTimeNanos);
        }
        
        return getCurrentTimestamp();
    }
}