package com.bandhub.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.bandhub.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException exception, WebRequest request){
        HttpStatus status = HttpStatus.CONFLICT;

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            exception.getMessage(),
            request.getDescription(false).replace("uri=", ""),
            LocalDateTime.now(),
            null
        );

        return ResponseEntity
            .status(status)
            .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
            .getFieldErrors()
            .forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            "Validation failed",
            request.getDescription(false).replace("uri=", ""),
            LocalDateTime.now(),
            errors
        );

        return ResponseEntity
            .status(status)
            .body(response);


    }

}
