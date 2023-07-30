package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

public class NationalitiesNotFoundException
        extends ResourceNotFoundException {

    private ExceptionCode exceptionCode;

    public NationalitiesNotFoundException() {
        super(MemberExceptionCode.NATIONALITIES_NOT_FOUND);
        this.exceptionCode = MemberExceptionCode.NATIONALITIES_NOT_FOUND;
    }

    @Override
    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}