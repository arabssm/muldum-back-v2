package co.kr.muldum.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_AUTH_CODE(HttpStatus.UNAUTHORIZED, "잘못된 인증 코드입니다."),
    UNAUTHORIZED_DOMAIN(HttpStatus.UNAUTHORIZED, "허용되지 않은 이메일 도메인입니다."),
    UNREGISTERED_USER(HttpStatus.UNAUTHORIZED, "등록되지 않은 사용자입니다."),
    GOOGLE_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "구글 액세스 토큰이 만료되었습니다."),
    GOOGLE_API_ERROR(HttpStatus.BAD_GATEWAY, "구글 API 호출 중 오류가 발생했습니다.");

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
