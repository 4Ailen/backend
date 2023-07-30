package com.aliens.friendship.domain.emailauthentication.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.domain.emailauthentication.exception.EmailAuthenticationExceptionCode.EMAIL_INVALID_TOKEN;

public class EmailInvalidTokenException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public EmailInvalidTokenException() {
        super(EMAIL_INVALID_TOKEN.getMessage());
        this.exceptionCode = EMAIL_INVALID_TOKEN;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}