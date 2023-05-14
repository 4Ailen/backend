package com.aliens.friendship.domain.matching.exception;

import com.aliens.friendship.global.error.ErrorResponse;
import com.aliens.friendship.global.error.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MatchingExceptionHandler {

    /**
     * DuplicatedMatchException handling (Custom Exception)
     */
    @ExceptionHandler(DuplicatedMatchException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicatedMatchException(
            DuplicatedMatchException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(exceptionCode, exceptionCode.getMessage()),
                HttpStatus.valueOf(exceptionCode.getHttpStatus().value())
        );
    }

    /**
     * LanguageNotFoundException handling (Custom Exception)
     */
    @ExceptionHandler(LanguageNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleLanguageNotFoundException(
            LanguageNotFoundException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(exceptionCode, exceptionCode.getMessage()),
                HttpStatus.valueOf(exceptionCode.getHttpStatus().value())
        );
    }
}