package com.aliens.friendship.global.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ListResult<T>
        extends CommonResult {

    private final List<T> data;

    private ListResult(
            final String message,
            final List<T> data
    ) {
        super(message);
        this.data = data;
    }

    public static <T> ListResult<T> of(
            final String message,
            final List<T> data
    ) {
        return new ListResult<>(
                message,
                data
        );
    }
}