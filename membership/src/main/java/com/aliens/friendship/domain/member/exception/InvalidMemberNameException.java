package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

public class InvalidMemberNameException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public InvalidMemberNameException() {
        this.exceptionCode = MemberExceptionCode.INVALID_MEMBER_NAME;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}