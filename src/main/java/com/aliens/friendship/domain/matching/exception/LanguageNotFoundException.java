package com.aliens.friendship.domain.matching.exception;

import com.aliens.friendship.global.error.ExceptionCode;

import static com.aliens.friendship.domain.matching.exception.MatchingExceptionCode.LANGUAGE_NOT_FOUND;

public class LanguageNotFoundException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public LanguageNotFoundException() {
        super(LANGUAGE_NOT_FOUND.getMessage());
        this.exceptionCode = LANGUAGE_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}