package com.aliens.friendship.domain.match.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.domain.match.exception.MatchExceptionCode.MATCHING_NOT_FOUND_EXCEPTION;
import static com.aliens.friendship.domain.match.exception.MatchExceptionCode.MATCH_REQUEST_NOT_SUBMITTED;

public class MatchRequestNotSubmitted
        extends ResourceNotFoundException {

    public MatchRequestNotSubmitted(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public MatchRequestNotSubmitted() {
        super(MATCH_REQUEST_NOT_SUBMITTED);
    }
}