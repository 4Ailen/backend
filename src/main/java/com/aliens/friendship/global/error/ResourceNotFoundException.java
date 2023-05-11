package com.aliens.friendship.global.error;

import javax.persistence.EntityNotFoundException;

/**
 * 존재하지 않는 자원 예외
 */
public class ResourceNotFoundException
        extends EntityNotFoundException {
    private final ExceptionCode exceptionCode;

    public ResourceNotFoundException(
            ExceptionCode exceptionCode
    ) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}