package co.kr.muldum.domain.file.exception;

public class FileSizeLimitExceededException extends RuntimeException {
  public FileSizeLimitExceededException(String message) {
    super(message);
  }
}
