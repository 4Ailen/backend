package com.aliens.friendship.domain.matching.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.domain.matching.exception.MatchingExceptionCode.MATCHING_NOT_FOUND_EXCEPTION;

public class MatchingNotFoundException
        extends ResourceNotFoundException {

    public MatchingNotFoundException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public MatchingNotFoundException() {
        super(MATCHING_NOT_FOUND_EXCEPTION);
    }
}