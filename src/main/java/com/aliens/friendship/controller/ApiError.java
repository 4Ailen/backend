package com.aliens.friendship.controller;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError {

    private final String MESSAGE = "message";
    private final String STATUS = "status";
    private final String msg;
    private final int status;

    public ApiError(String msg, int status) {
        this.msg = msg;
        this.status = status;
    }

    public ApiError(String msg, HttpStatus status) {
        this(msg, status.value());
    }

    public ApiError(Throwable throwable, HttpStatus status) {
        this(throwable.getMessage(), status.value());
    }
}