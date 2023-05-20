package com.aliens.friendship.domain.member.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionCode
        implements ExceptionCode {

    DUPLICATE_MEMBER_EMAIL(CONFLICT, "MB-C-001", "이미 사용중인 이메일입니다."),
    INVALID_MEMBER_PASSWORD(BAD_REQUEST, "MB-C-002", "비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "MB-C-003", "존재하지 않는 회원입니다."),
    PASSWORD_CHANGE_FAILED_EXCEPTION(BAD_REQUEST, "MB-C-004", "비밀번호 변경에 실패하였습니다."),
    EMAIL_VERIFICATION_NOT_COMPLETED(FORBIDDEN, "MB-C-005", "이메일 인증이 완료되지 않았습니다."),
    WITHDRAWN_MEMBER_WITHIN_A_WEEK(CONFLICT, "MB-C-006", "일주일 내에 탈퇴한 회원은 동일한 이메일로 가입할 수 없습니다."),
    INVALID_MEMBER_NAME(BAD_REQUEST, "MB-C-007", "회원의 이름이 일치하지 않습니다."),
    NATIONALITIES_NOT_FOUND(NOT_FOUND, "NTN-C-001", "국적 목록이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}