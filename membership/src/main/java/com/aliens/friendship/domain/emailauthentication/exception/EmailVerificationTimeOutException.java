package com.aliens.friendship.domain.emailauthentication.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.domain.emailauthentication.exception.EmailAuthenticationExceptionCode.*;

public class EmailVerificationTimeOutException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public EmailVerificationTimeOutException() {
        super(EMAIL_VERIFICATION_TIME_OUT.getMessage());
        this.exceptionCode = EMAIL_VERIFICATION_TIME_OUT;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}