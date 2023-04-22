package com.aliens.friendship.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionCode
        implements ExceptionCode {

    INVALID_REQUEST_PARAMETER(BAD_REQUEST, "CM-C-001", "유효하지 않은 요청 파라미터입니다."),
    INVALID_REQUEST_METHOD(METHOD_NOT_ALLOWED, "CM-C-002", "유효하지 않은 http 요청 메소드입니다."),
    INVALID_RESOURCE_OWNER(FORBIDDEN, "CM-C-003", "해당 리소스를 처리할 권한이 없습니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "CM-S-001", "Internal Server Error");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}