package co.kr.muldum.domain.file.exception;

public class InvalidFileTypeException extends RuntimeException {
  public InvalidFileTypeException(String message) {
    super(message);
  }
}
