package com.aliens.friendship.domain.emailauthentication.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.domain.emailauthentication.exception.EmailAuthenticationExceptionCode.*;

public class EmailAlreadyRegisteredException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public EmailAlreadyRegisteredException() {
        super(EMAIL_ALREADY_REGISTERED.getMessage());
        this.exceptionCode = EMAIL_ALREADY_REGISTERED;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}