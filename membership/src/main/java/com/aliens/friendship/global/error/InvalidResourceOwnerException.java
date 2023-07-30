package com.aliens.friendship.global.error;

/**
 * 유효하지 않은 자원 소유자 예외
 */
public class InvalidResourceOwnerException
        extends RuntimeException {
    private final ExceptionCode code;

    public InvalidResourceOwnerException(
            ExceptionCode exceptionCode
    ) {
        super(exceptionCode.getMessage());
        this.code = exceptionCode;
    }

    public ExceptionCode getCode() {
        return code;
    }
}