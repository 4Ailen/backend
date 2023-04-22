package com.aliens.friendship.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.member.exception.MemberExceptionCode.*;

public class PasswordChangeFailedException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public PasswordChangeFailedException() {
        this.exceptionCode = PASSWORD_CHANGE_FAILED_EXCEPTION;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}