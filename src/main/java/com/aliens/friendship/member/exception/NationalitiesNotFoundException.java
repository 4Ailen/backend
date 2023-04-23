package com.aliens.friendship.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.member.exception.MemberExceptionCode.*;

public class NationalitiesNotFoundException
        extends ResourceNotFoundException {

    private ExceptionCode exceptionCode;

    public NationalitiesNotFoundException() {
        super(NATIONALITIES_NOT_FOUND);
        this.exceptionCode = NATIONALITIES_NOT_FOUND;
    }

    @Override
    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}