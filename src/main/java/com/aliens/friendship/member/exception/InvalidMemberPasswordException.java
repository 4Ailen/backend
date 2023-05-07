package com.aliens.friendship.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.member.exception.MemberExceptionCode.*;

public class InvalidMemberPasswordException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public InvalidMemberPasswordException() {
        super(INVALID_MEMBER_PASSWORD.getMessage());
        this.exceptionCode = INVALID_MEMBER_PASSWORD;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}