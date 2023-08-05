package com.aliens.friendship.domain.auth.exception;


import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.*;

public class FcmTokenNotFoundException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public FcmTokenNotFoundException() {
        super(REQUEST_FCM_TOKEN_NOT_FOUND.getMessage());
        exceptionCode = REQUEST_FCM_TOKEN_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}