package com.aliens.friendship.global.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommonResult {
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    protected CommonResult(final String message) {
        this.message = message;
    }

    public static CommonResult of(final String message) {
        return new CommonResult(message);
    }
}