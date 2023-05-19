package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MemberExceptionHandler {

    /**
     * DuplicateMemberEmailException 핸들링
     * Custom Exception
     */
    @ExceptionHandler(DuplicateMemberEmailException.class)
    protected ResponseEntity<ErrorResponse> handlingDuplicateMemberEmailException(
            DuplicateMemberEmailException e
    ) {
        log.error("[handling DuplicateMemberEmailException] {}", e.getExceptionCode().getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(e.getExceptionCode()),
                HttpStatus.valueOf(e.getExceptionCode().getHttpStatus().value())
        );
    }

    /**
     * InvalidMemberPasswordException 핸들링
     * Custom Exception
     */
    @ExceptionHandler(InvalidMemberPasswordException.class)
    protected ResponseEntity<ErrorResponse> handlingInvalidMemberPasswordException(
            InvalidMemberPasswordException e
    ) {
        log.error("[handling InvalidMemberPasswordException] {}", e.getExceptionCode().getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(e.getExceptionCode()),
                HttpStatus.valueOf(e.getExceptionCode().getHttpStatus().value())
        );
    }

    /**
     * PasswordChangeFailedException 핸들링
     * Custom Exception
     */
    @ExceptionHandler(PasswordChangeFailedException.class)
    protected ResponseEntity<ErrorResponse> handlingPasswordChangeFailedException(
            PasswordChangeFailedException e
    ) {
        log.error("[handling PasswordChangeFailedException] {}", e.getExceptionCode().getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(e.getExceptionCode()),
                HttpStatus.valueOf(e.getExceptionCode().getHttpStatus().value())
        );
    }

    /**
     * WithdrawnWithinAWeekException 핸들링
     * Custom Exception
     */
    @ExceptionHandler(WithdrawnMemberWithinAWeekException.class)
    protected ResponseEntity<ErrorResponse> handlingWithdrawnWithinAWeekException(
            WithdrawnMemberWithinAWeekException e
    ) {
        log.error("[handling WithdrawnWithinAWeekException] {}", e.getExceptionCode().getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(e.getExceptionCode()),
                HttpStatus.valueOf(e.getExceptionCode().getHttpStatus().value())
        );
    }

    /**
     * EmailVerificationException 핸들링
     * Custom Exception
     */
    @ExceptionHandler(EmailVerificationException.class)
    protected ResponseEntity<ErrorResponse> handlingEmailVerificationException(
            EmailVerificationException e
    ) {
        log.error("[handling EmailVerificationException] {}", e.getExceptionCode().getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(e.getExceptionCode()),
                HttpStatus.valueOf(e.getExceptionCode().getHttpStatus().value())
        );
    }
}