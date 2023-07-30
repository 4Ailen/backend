package com.aliens.friendship.domain.match.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.domain.match.exception.MatchExceptionCode.DUPLICATED_MATCH;

public class DuplicatedMatchException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public DuplicatedMatchException() {
        super(DUPLICATED_MATCH.getMessage());
        this.exceptionCode = DUPLICATED_MATCH;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}