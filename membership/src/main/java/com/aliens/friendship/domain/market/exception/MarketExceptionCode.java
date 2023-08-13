package com.aliens.friendship.domain.market.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum MarketExceptionCode
        implements ExceptionCode {

    MARKET_ARTICLE_NOT_FOUND(NOT_FOUND, "MT-C-001", "존재하지 않는 판매글입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}