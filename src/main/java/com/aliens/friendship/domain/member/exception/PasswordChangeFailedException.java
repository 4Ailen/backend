package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

public class PasswordChangeFailedException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public PasswordChangeFailedException() {
        this.exceptionCode = MemberExceptionCode.PASSWORD_CHANGE_FAILED_EXCEPTION;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}