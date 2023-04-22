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
     * 존재하지 않는 자원 접근 예외 핸들링
     * Custom Exception
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException e
    ) {
        log.error("handle ResourceNotFoundException");
        return new ResponseEntity<>(
                ErrorResponse.of(e.getCode()),
                HttpStatus.valueOf(e.getCode().getHttpStatus().value())
        );
    }

    /**
     * 요청 파라미터 검증 예외 핸들링
     * Built-In Exception
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("handle BindException");
        return new ResponseEntity<>(
                ErrorResponse.of(INVALID_REQUEST_PARAMETER, e.getBindingResult()),
                HttpStatus.valueOf(INVALID_REQUEST_PARAMETER.getHttpStatus().value())
        );
    }

    /**
     * 유효하지 않은 HTTP Method 요청 예외 핸들링
     * Built-In Exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        log.error("handle HttpRequestMethodNotSupportedException");
        return new ResponseEntity<>(
                ErrorResponse.of(INVALID_REQUEST_METHOD),
                HttpStatus.valueOf(INVALID_REQUEST_METHOD.getHttpStatus().value())
        );
    }

    /**
     * 유효하지 않은 리소스 소유자의 요청 예외 핸들링
     * Custom Exception
     */
    @ExceptionHandler(InvalidResourceOwnerException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidResourceOwnerException(
            InvalidResourceOwnerException e
    ) {
        log.error("handle InvalidResourceOwnerException", e);
        return new ResponseEntity<>(
                ErrorResponse.of(e.getCode()),
                HttpStatus.valueOf(e.getCode().getHttpStatus().value())
        );
    }

    /**
     * 최상위 예외 핸들링
     * Built-In Exception
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(
            Exception e
    ) {
        log.error("handle Exception", e);
        return new ResponseEntity<>(
                ErrorResponse.of(SERVER_ERROR),
                HttpStatus.valueOf(SERVER_ERROR.getHttpStatus().value())
        );
    }
}