package com.aliens.friendship.domain.auth.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.*;

public class MemberPasswordMisMatchException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public MemberPasswordMisMatchException() {
        super(MEMBER_PASSWORD_MISMATCH.getMessage());
        exceptionCode = MEMBER_PASSWORD_MISMATCH;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}