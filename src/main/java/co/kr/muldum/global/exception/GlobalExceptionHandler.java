package co.kr.muldum.global.exception;

import co.kr.muldum.domain.file.exception.FileSizeLimitExceededException;
import co.kr.muldum.domain.file.exception.InvalidFileTypeException;
import co.kr.muldum.presentation.report.exception.UnauthorizedRoleException;
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
                        "errorCode", ex.getErrorCode().name(),
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler({
            UnauthorizedRoleException.class,
            UnauthorizedTeamAccessException.class,
            UnauthorizedReportAccessException.class
    })
    public ResponseEntity<Map<String, Object>> handleReportAccessDenied(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "statusCode", HttpStatus.FORBIDDEN.value(),
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(MonthReportNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMonthReportNotFound(MonthReportNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "statusCode", HttpStatus.NOT_FOUND.value(),
                        "message", ex.getMessage()
                ));
    }
}
