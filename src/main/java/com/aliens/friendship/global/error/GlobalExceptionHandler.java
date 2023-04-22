package com.aliens.friendship.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.aliens.friendship.global.error.GlobalExceptionCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ResourceNotFoundException 핸들링
     * Custom Exception
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handlingResourceNotFoundException(
            ResourceNotFoundException e
    ) {
        log.error("[handling ResourceNotFoundException] {}", e.getExceptionCode().getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(e.getExceptionCode()),
                HttpStatus.valueOf(e.getExceptionCode().getHttpStatus().value())
        );
    }

    /**
     * InvalidResourceOwnerException 핸들링
     * Custom Exception
     */
    @ExceptionHandler(InvalidResourceOwnerException.class)
    protected ResponseEntity<ErrorResponse> handlingInvalidResourceOwnerException(
            InvalidResourceOwnerException e
    ) {
        log.error("[handling InvalidResourceOwnerException] {}", e.getCode().getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(e.getCode()),
                HttpStatus.valueOf(e.getCode().getHttpStatus().value())
        );
    }

    /**
     * BindException 핸들링
     * Built-In Exception
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handlingBindException(BindException e) {
        log.error("[handling BindException] {}", INVALID_REQUEST_PARAMETER.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(INVALID_REQUEST_PARAMETER, e.getBindingResult()),
                HttpStatus.valueOf(INVALID_REQUEST_PARAMETER.getHttpStatus().value())
        );
    }

    /**
     * HttpRequestMethodNotSupportedException 핸들링
     * Built-In Exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handlingHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        log.error("[handling HttpRequestMethodNotSupportedException] {}", INVALID_REQUEST_METHOD.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(INVALID_REQUEST_METHOD),
                HttpStatus.valueOf(INVALID_REQUEST_METHOD.getHttpStatus().value())
        );
    }

    /**
     * Exception 핸들링
     * Built-In Exception
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handlingException(
            Exception e
    ) {
        log.error("[handling Exception]", e);
        return new ResponseEntity<>(
                ErrorResponse.of(SERVER_ERROR),
                HttpStatus.valueOf(SERVER_ERROR.getHttpStatus().value())
        );
    }
}