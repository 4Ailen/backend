package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

public class IdenticalPasswordException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public IdenticalPasswordException() {
        this.exceptionCode = MemberExceptionCode.IDENTICAL_PASSWORD;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}