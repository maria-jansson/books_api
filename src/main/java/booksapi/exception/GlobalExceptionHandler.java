package booksapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
          ResourceNotFoundException exception) {
    LocalDateTime timestamp = LocalDateTime.now();
    ErrorResponse error = new ErrorResponse(404, exception.getMessage(), timestamp);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(UsernameAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(
          UsernameAlreadyExistsException exception) {
    LocalDateTime timestamp = LocalDateTime.now();
    ErrorResponse error = new ErrorResponse(409, exception.getMessage(), timestamp);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
          InvalidCredentialsException exception) {
    LocalDateTime timestamp = LocalDateTime.now();
    ErrorResponse error = new ErrorResponse(401, exception.getMessage(), timestamp);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable() {
    LocalDateTime timestamp = LocalDateTime.now();
    ErrorResponse error = new ErrorResponse(
            400,
            "Invalid or missing request body",
            timestamp);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
          MethodArgumentNotValidException exception) {
    LocalDateTime timestamp = LocalDateTime.now();
    String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
    ErrorResponse error = new ErrorResponse(400, message, timestamp);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException() {
    LocalDateTime timestamp = LocalDateTime.now();
    ErrorResponse error = new ErrorResponse(
            500,
            "An unexpected error occurred",
            timestamp);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
