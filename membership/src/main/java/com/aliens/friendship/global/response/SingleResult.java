package com.aliens.friendship.global.response;

import lombok.Getter;

@Getter
public class SingleResult<T>
        extends CommonResult {

    private final T data;

    private SingleResult(
            final String message,
            final T data
    ) {
        super(message);
        this.data = data;
    }

    public static <T> SingleResult<T> of(
            final String message,
            final T data
    ) {
        return new SingleResult<>(
                message,
                data
        );
    }
}