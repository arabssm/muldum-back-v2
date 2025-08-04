package co.kr.muldum.global.exception;

public class UnauthorizedDomainException extends RuntimeException {
    public UnauthorizedDomainException(String message) {
        super(message);
    }
}
