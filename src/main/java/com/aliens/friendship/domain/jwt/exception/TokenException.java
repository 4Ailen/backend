package com.aliens.friendship.domain.jwt.exception;

import com.aliens.friendship.global.error.ExceptionCode;

public class TokenException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public TokenException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}