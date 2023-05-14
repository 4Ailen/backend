package com.aliens.friendship.domain.jwt.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum JWTExceptionCode
        implements ExceptionCode {

    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "AT-C-001", "Refresh Token이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "AT-C-002", "유효하지 않은 Refresh Token 입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}