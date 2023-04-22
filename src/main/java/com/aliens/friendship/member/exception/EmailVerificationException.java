package com.aliens.friendship.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.member.exception.MemberExceptionCode.EMAIL_VERIFICATION_NOT_COMPLETED;

public class EmailVerificationException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public EmailVerificationException() {
        this.exceptionCode = EMAIL_VERIFICATION_NOT_COMPLETED;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}