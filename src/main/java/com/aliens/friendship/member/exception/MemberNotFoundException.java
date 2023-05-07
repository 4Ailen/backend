package com.aliens.friendship.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

public class MemberNotFoundException
        extends ResourceNotFoundException {

    private final ExceptionCode exceptionCode;

    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND);
        exceptionCode = MEMBER_NOT_FOUND;
    }

    @Override
    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}