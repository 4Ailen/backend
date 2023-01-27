package com.aliens.friendship.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private final String MESSAGE = "message";
    private final String STATUS = "status";
    private final String msg;
    private final int status;

    public ErrorResponse(String msg, int status) {
        this.msg = msg;
        this.status = status;
    }

    public ErrorResponse(String msg, HttpStatus status) {
        this(msg, status.value());
    }

    public ErrorResponse(Throwable throwable, HttpStatus status) {
        this(throwable.getMessage(), status.value());
    }
}