package co.kr.muldum.presentation.report.exception;

public class UnauthorizedRoleException extends RuntimeException {

    public UnauthorizedRoleException(String role) {
        super(role == null ? "권한이 없습니다." : "권한이 없습니다: " + role);
    }
}
