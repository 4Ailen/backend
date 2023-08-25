package com.aliens.friendship.domain.notice.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum NoticeExceptionCode
        implements ExceptionCode {

    NOTICE_NOT_FOUND(NOT_FOUND, "BA-C-001", "존재하지 않는 공지사항입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}