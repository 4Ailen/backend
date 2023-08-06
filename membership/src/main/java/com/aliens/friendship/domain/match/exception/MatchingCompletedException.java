package com.aliens.friendship.domain.match.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.domain.match.exception.MatchExceptionCode.MATCHING_COMPLETED;
import static com.aliens.friendship.domain.match.exception.MatchExceptionCode.MATCHING_NOT_FOUND_EXCEPTION;

public class MatchingCompletedException
        extends ResourceNotFoundException {

    public MatchingCompletedException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public MatchingCompletedException() {
        super(MATCHING_COMPLETED);
    }
}