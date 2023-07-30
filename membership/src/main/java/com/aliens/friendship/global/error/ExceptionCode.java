package com.aliens.friendship.global.error;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {

    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();
}