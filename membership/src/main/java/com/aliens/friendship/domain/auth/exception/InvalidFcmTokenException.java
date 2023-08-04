package com.aliens.friendship.domain.auth.exception;


import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.INVALID_FCM_TOKEN;

public class InvalidFcmTokenException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public InvalidFcmTokenException() {
        super(INVALID_FCM_TOKEN.getMessage());
        exceptionCode = INVALID_FCM_TOKEN;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}