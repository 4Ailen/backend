package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;

public class DuplicateMemberEmailException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public DuplicateMemberEmailException() {
        super(MemberExceptionCode.DUPLICATE_MEMBER_EMAIL.getMessage());
        exceptionCode = MemberExceptionCode.DUPLICATE_MEMBER_EMAIL;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}