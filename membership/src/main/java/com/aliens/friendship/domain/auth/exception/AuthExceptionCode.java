package com.aliens.friendship.domain.auth.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum AuthExceptionCode
        implements ExceptionCode {

    /**
     * JWT
     * 1 ~ 99
     */
    INVALID_TOKEN(UNAUTHORIZED, "AT-C-001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "AT-C-002", "만료된 토큰입니다."),
    NOT_EXPIRED_TOKEN(BAD_REQUEST, "AT-C-003", "만료되지 않은 토큰입니다."),
    REQUEST_TOKEN_NOT_FOUND(BAD_REQUEST, "AT-C-004", "요청에 토큰이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "AT-C-005", "유효하지 않은 리프레쉬 토큰입니다."),
    UNTRUSTED_CREDENTIAL(UNAUTHORIZED, "AT-C-006", "신뢰할 수 없는 자격증명 입니다."),
    LOGGED_OUT_TOKEN(UNAUTHORIZED, "AT-C-007", "로그아웃된 토큰입니다."),

    /**
     * MEMBER
     * 100 ~ 199
     */
    INVALID_MEMBER_ROLE(FORBIDDEN, "AT-C-100", "유효하지 않은 사용자 권한입니다."),
    NOT_AUTHORIZATION_USER(NOT_FOUND, "AT-C-101", "인가된 사용자가 아닙니다."),
    MEMBER_PASSWORD_MISMATCH(UNAUTHORIZED, "AT-C-102", "일치하지 않는 패스워드입니다."),

    /**
     * FCM
     * 200 ~ 299
     */
    REQUEST_FCM_TOKEN_NOT_FOUND(BAD_REQUEST, "AT-C-200", "요청에 FCM 토큰이 존재하지 않습니다."),
    INVALID_FCM_TOKEN(BAD_REQUEST, "AT-C-201", "유효하지 않은 FCM 토큰입니다."),


    /**
     * Common Exception
     * 300 ~
     */
    AUTHENTICATION_ERROR(UNAUTHORIZED, "AT-C-300", "Authentication exception."),
    INTERNAL_AUTHENTICATION_SERVICE_EXCEPTION(INTERNAL_SERVER_ERROR, "AT-S-300", "Internal authentication service exception.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    AuthExceptionCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}