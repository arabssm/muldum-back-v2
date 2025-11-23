package co.kr.muldum.global.exception;

public class UnauthorizedReportAccessException extends RuntimeException {

    public UnauthorizedReportAccessException(Long userId, Long reportId) {
        super(buildMessage(userId, reportId));
    }

    private static String buildMessage(Long userId, Long reportId) {
        return String.format("유저 %d는 월말 평가 %d에 접근할 수 없습니다.", userId, reportId);
    }
}
