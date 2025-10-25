package co.kr.muldum.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력 값입니다."),
    INVALID_AUTH_CODE(HttpStatus.UNAUTHORIZED, "잘못된 인증 코드입니다."),
    LOGIN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "로그인에 실패했습니다."),
    USER_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 생성에 실패했습니다."),
    UNREGISTERED_USER(HttpStatus.UNAUTHORIZED, "등록되지 않은 사용자입니다."),
    INVALID_GOOGLE_SHEET_URL(HttpStatus.BAD_REQUEST, "잘못된 구글 시트 URL입니다."),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "잘못된 역할입니다."),
    NOT_FOUND_COLUMN(HttpStatus.NOT_FOUND, "컬럼이 일치하지 않거나 찾을 수 없습니다."),
    NOT_TEAM_MEMBER(HttpStatus.FORBIDDEN, "해당 팀의 멤버가 아닙니다."),
    TEAM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "팀에 대한 접근 권한이 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_PRODUCT_LINK(HttpStatus.BAD_REQUEST, "상품 링크가 유효하지 않습니다.");

  private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
