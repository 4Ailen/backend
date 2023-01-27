package com.aliens.friendship.global.common;

import com.aliens.friendship.global.error.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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

    public static <T> Response<T> SUCCESS(T response){
        return new Response<>(true, response, null);
    }

    public static <T> Response<T> ERROR(String msg, HttpStatus status){
        return new Response<>(false, null, new ErrorResponse(msg, status));
    }

    public static <T> Response<T> ERROR(Throwable throwable, HttpStatus status){
        return new Response<>(false, null, new ErrorResponse(throwable, status));
    }
}