package co.kr.muldum.global.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Component
public class SnowflakeIdGenerator {

    private static final long EPOCH = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
            .atZone(ZoneId.of("Asia/Seoul"))
            .toInstant()
            .toEpochMilli() / 1000 / 60;

    private static final int TIMESTAMP_BITS = 26;
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
        return LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli() / 1000 / 60 - EPOCH;
    }

    private long waitForNextMinute(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
}