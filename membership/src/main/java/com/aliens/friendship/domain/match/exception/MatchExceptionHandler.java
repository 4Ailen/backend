package com.aliens.friendship.domain.match.exception;

import com.aliens.friendship.global.error.ErrorResponse;
import com.aliens.friendship.global.error.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MatchExceptionHandler {

    @ExceptionHandler(MatchRequestNotSubmitted.class)
    protected ResponseEntity<ErrorResponse> handleMatchRequestNotSubmittedException(
            MatchRequestNotSubmitted e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(exceptionCode, exceptionCode.getMessage()),
                HttpStatus.valueOf(exceptionCode.getHttpStatus().value())
        );
    }

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

    @ExceptionHandler(MatchingCompletedException.class)
    protected ResponseEntity<ErrorResponse> handleMatchingCompletedException(
            MatchingCompletedException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(exceptionCode, exceptionCode.getMessage()),
                HttpStatus.valueOf(exceptionCode.getHttpStatus().value())
        );
    }

}