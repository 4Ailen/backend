package com.aliens.friendship.global.common;

import com.aliens.friendship.global.error.ErrorResponse;
import lombok.Getter;

@Getter
public class Response<T> {

    private final boolean success;

    private final T response;

    private final ErrorResponse error;

    public Response(boolean success, T response, ErrorResponse error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }

    public static <T> Response<T> SUCCESS(T response) {
        return new Response<>(true, response, null);
    }
}