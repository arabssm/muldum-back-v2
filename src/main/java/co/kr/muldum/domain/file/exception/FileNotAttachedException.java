package co.kr.muldum.domain.file.exception;

public class FileNotAttachedException extends RuntimeException {
  public FileNotAttachedException(String message) {
    super(message);
  }
}
