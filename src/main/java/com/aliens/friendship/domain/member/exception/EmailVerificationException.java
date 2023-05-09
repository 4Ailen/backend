package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

public class EmailVerificationException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public EmailVerificationException() {
        this.exceptionCode = MemberExceptionCode.EMAIL_VERIFICATION_NOT_COMPLETED;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}