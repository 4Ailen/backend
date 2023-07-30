package com.aliens.friendship.domain.auth.exception;


import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.INVALID_REFRESH_TOKEN;

public class RefreshTokenNotFoundException
        extends ResourceNotFoundException {

    private final ExceptionCode exceptionCode;

    public RefreshTokenNotFoundException() {
        super(INVALID_REFRESH_TOKEN);
        exceptionCode = INVALID_REFRESH_TOKEN;
    }

    @Override
    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}