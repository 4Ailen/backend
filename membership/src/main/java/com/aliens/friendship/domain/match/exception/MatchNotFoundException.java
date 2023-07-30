package com.aliens.friendship.domain.match.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.domain.match.exception.MatchExceptionCode.MATCHING_NOT_FOUND_EXCEPTION;

public class MatchNotFoundException
        extends ResourceNotFoundException {

    public MatchNotFoundException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public MatchNotFoundException() {
        super(MATCHING_NOT_FOUND_EXCEPTION);
    }
}