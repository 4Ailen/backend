package com.aliens.friendship.domain.match.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum MatchExceptionCode implements ExceptionCode {

    MATCHING_NOT_FOUND_EXCEPTION(NOT_FOUND, "MT-C-001", "존재하지 않는 매칭정보입니다."),
    MATCHED_PARTNER_NOT_FOUND(NOT_FOUND, "MT-C-002", "매칭된 파트너가 없습니다."),
    MATCH_NOT_COMPLETED(BAD_REQUEST, "MT-C-003", "매칭이 완료되지 않은 사용자입니다."),
    MATCH_REQUEST_NOT_SUBMITTED(BAD_REQUEST, "MT-C-004", "매칭 신청을 하지 않은 사용자입니다."),
    APPLICANT_NOT_FOUND(NOT_FOUND, "MT-C-005", "매칭 신청자의 정보가 없습니다."),
    DUPLICATED_MATCH(BAD_REQUEST, "MT-C-006", "이미 매칭을 신청한 사용자입니다."),
    LANGUAGE_NOT_FOUND(NOT_FOUND, "MT-C-007", "존재하지 않는 언어입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}