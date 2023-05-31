package com.aliens.friendship.global.response;

import lombok.Getter;

/**
 * 성공 결과만 반환하는 응답
 */
@Getter
public class CommonResult {
    private final Integer status;
    private final String message;

    protected CommonResult(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public static CommonResult of(
            Integer status,
            String message
    ) {
        return new CommonResult(status, message);
    }
}