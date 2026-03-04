package com.github.maria_jansson.booksapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse error = new ErrorResponse(404, exception.getMessage(), timestamp);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
