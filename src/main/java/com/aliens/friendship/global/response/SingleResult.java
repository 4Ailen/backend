package com.aliens.friendship.global.response;

import lombok.Getter;

/**
 * 단건 결과값만 반환하는 응답
 */
@Getter
public class SingleResult<T> extends CommonResult {

    private final T data;

    private SingleResult(
            Integer status,
            String message,
            T data
    ) {
        super(status, message);
        this.data = data;
    }

    public static <T> SingleResult<T> of(
            Integer status,
            String message,
            T data
    ) {
        return new SingleResult<>(status, message, data);
    }
}