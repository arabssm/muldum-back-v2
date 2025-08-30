package co.kr.muldum.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_AUTH_CODE(HttpStatus.UNAUTHORIZED, "잘못된 인증 코드입니다."),
    UNAUTHORIZED_DOMAIN(HttpStatus.UNAUTHORIZED, "허용되지 않은 이메일 도메인입니다."),
    UNREGISTERED_USER(HttpStatus.UNAUTHORIZED, "등록되지 않은 사용자입니다."),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다."),
    INVALID_GOOGLE_SHEET_URL(HttpStatus.BAD_REQUEST, "잘못된 구글 시트 URL입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
