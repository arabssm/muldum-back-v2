package co.kr.muldum.global.exception;

public class MonthReportNotFoundException extends RuntimeException {

    public MonthReportNotFoundException(Long reportId) {
        super(buildMessage(reportId));
    }

    private static String buildMessage(Long reportId) {
        if (reportId == null) {
            return "월말 평가를 찾을 수 없습니다.";
        }
        return String.format("월말 평가(ID: %d)를 찾을 수 없습니다.", reportId);
    }
}
