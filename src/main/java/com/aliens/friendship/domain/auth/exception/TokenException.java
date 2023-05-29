package com.aliens.friendship.domain.auth.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import io.jsonwebtoken.Claims;

public class TokenException
        extends RuntimeException {

    private final ExceptionCode exceptionCode;
    private Claims expiredTokenClaims;

    public TokenException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public TokenException(
            ExceptionCode exceptionCode,
            Claims expiredTokenClaims
    ) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
        this.expiredTokenClaims = expiredTokenClaims;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public Claims getExpiredTokenClaims() {
        return expiredTokenClaims;
    }
}