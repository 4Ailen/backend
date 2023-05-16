package com.aliens.friendship.domain.jwt.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.domain.jwt.exception.JWTExceptionCode.*;

public class RefreshTokenNotFoundException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public RefreshTokenNotFoundException() {
        super(REFRESH_TOKEN_NOT_FOUND.getMessage());
        this.exceptionCode = REFRESH_TOKEN_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}