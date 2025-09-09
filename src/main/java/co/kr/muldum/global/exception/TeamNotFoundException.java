package co.kr.muldum.global.exception;

public class TeamNotFoundException extends RuntimeException {
  public TeamNotFoundException(String message) {
    super(message);
  }
}
