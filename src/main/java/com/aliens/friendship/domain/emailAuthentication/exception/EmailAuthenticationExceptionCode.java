package com.aliens.friendship.domain.emailAuthentication.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum EmailAuthenticationExceptionCode implements ExceptionCode {

    EMAIL_ALREADY_REGISTERED(BAD_REQUEST, "EAT-C-001", "이미 회원가입된 이메일입니다."),
    EMAIL_VERIFICATION_TIME_OUT(UNAUTHORIZED, "EAT-C-002", "이메일 인증 시간이 초과되었습니다."),
    EMAIL_INVALID_TOKEN(UNAUTHORIZED, "EAT-C-003", "유효하지 않은 이메일 인증 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}