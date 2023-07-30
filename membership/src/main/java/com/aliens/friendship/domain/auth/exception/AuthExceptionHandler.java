package com.aliens.friendship.domain.auth.exception;

import com.aliens.friendship.global.error.ErrorResponse;
import com.aliens.friendship.global.error.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.INTERNAL_AUTHENTICATION_SERVICE_EXCEPTION;


@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    /**
     * TokenException handling (Custom Exception)
     */
    @ExceptionHandler(TokenException.class)
    protected ResponseEntity<ErrorResponse> handleTokenException(
            TokenException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(exceptionCode, exceptionCode.getMessage()),
                HttpStatus.valueOf(exceptionCode.getHttpStatus().value())
        );
    }

    /**
     * MemberPasswordMisMatchException handling (Custom Exception)
     */
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMemberPasswordMisMatchException(
            MemberPasswordMisMatchException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(exceptionCode, exceptionCode.getMessage()),
                HttpStatus.valueOf(exceptionCode.getHttpStatus().value())
        );
    }

    /**
     * InternalAuthenticationServiceException handling (Built-In Exception)
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    protected ResponseEntity<ErrorResponse> handleInternalAuthenticationServiceException(
            InternalAuthenticationServiceException e
    ) {
        ExceptionCode exceptionCode = INTERNAL_AUTHENTICATION_SERVICE_EXCEPTION;
        log.error("{}", e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(exceptionCode, exceptionCode.getMessage()),
                HttpStatus.valueOf(exceptionCode.getHttpStatus().value())
        );
    }
}