package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

public class InvalidMemberPasswordException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public InvalidMemberPasswordException() {
        super(MemberExceptionCode.INVALID_MEMBER_PASSWORD.getMessage());
        this.exceptionCode = MemberExceptionCode.INVALID_MEMBER_PASSWORD;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}