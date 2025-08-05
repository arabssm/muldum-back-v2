package co.kr.muldum.global.exception;

import co.kr.muldum.domain.file.exception.FileSizeLimitExceededException;
import co.kr.muldum.domain.file.exception.InvalidFileTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(FileSizeLimitExceededException.class)
  public ResponseEntity<Map<String, Object>> handleFileSizeLimitExceeded(FileSizeLimitExceededException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                    "statusCode", 500,
                    "message", ex.getMessage()
            ));
  }

  @ExceptionHandler(InvalidFileTypeException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidFileType(InvalidFileTypeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                    "statusCode", 400,
                    "message", ex.getMessage()
            ));
  }
}
