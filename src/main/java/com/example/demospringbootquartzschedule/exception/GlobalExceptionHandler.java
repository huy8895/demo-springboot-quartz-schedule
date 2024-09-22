package com.example.demospringbootquartzschedule.exception;

import com.example.demospringbootquartzschedule.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.ObjectAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleJobAlreadyExistsException(ObjectAlreadyExistsException ex, WebRequest request) {
        log.error("handleJobAlreadyExistsException", ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SchedulerException.class)
    public ResponseEntity<ErrorResponse> handleSchedulerException(SchedulerException ex, WebRequest request) {
        log.error("handleSchedulerException", ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("handleGenericException", ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}