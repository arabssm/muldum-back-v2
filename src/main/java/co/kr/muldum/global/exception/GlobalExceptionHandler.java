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
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(Map.of(
                    "statusCode", 413,
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
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTokenNotFound(TokenNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "TOKEN_NOT_FOUND",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(Map.of(
                        "statusCode", ex.getErrorCode().getStatus().value(),
                        "message", ex.getErrorCode().getMessage()
                ));
    }
}
