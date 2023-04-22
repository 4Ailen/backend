package com.aliens.friendship.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.member.exception.MemberExceptionCode.DUPLICATE_MEMBER_EMAIL;

public class DuplicateMemberEmailException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public DuplicateMemberEmailException() {
        super(DUPLICATE_MEMBER_EMAIL.getMessage());
        exceptionCode = DUPLICATE_MEMBER_EMAIL;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}