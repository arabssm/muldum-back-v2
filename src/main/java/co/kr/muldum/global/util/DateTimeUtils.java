package co.kr.muldum.global.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {

    private DateTimeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 시작일시가 종료일시보다 이전인지 검증
     */
    public static boolean isStartBeforeEnd(LocalDateTime start, LocalDateTime end) {
        return start.isBefore(end);
    }

    /**
     * 날짜 범위가 최대 일수 이내인지 검증
     */
    public static boolean isWithinMaxDays(LocalDateTime start, LocalDateTime end, long maxDays) {
        long daysBetween = ChronoUnit.DAYS.between(start, end);
        return daysBetween <= maxDays;
    }

    /**
     * 두 일정이 시간적으로 겹치는지 확인
     */
    public static boolean isOverlapping(LocalDateTime start1, LocalDateTime end1,
                                        LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}

