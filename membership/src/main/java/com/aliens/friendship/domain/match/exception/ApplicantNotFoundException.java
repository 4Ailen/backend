package com.aliens.friendship.domain.match.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

import static com.aliens.friendship.domain.match.exception.MatchExceptionCode.*;

public class ApplicantNotFoundException
        extends ResourceNotFoundException {

    public ApplicantNotFoundException() {
        super(APPLICANT_NOT_FOUND);
    }

    public ApplicantNotFoundException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}