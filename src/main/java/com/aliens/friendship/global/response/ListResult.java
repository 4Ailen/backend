package com.aliens.friendship.global.response;

import lombok.Getter;

import java.util.List;

/**
 * 여러개의 결과값을 반환하는 응답
 */
@Getter
public class ListResult<T> extends CommonResult {

    private final List<T> data;

    private ListResult(Integer status, String message, List<T> data) {
        super(status, message);
        this.data = data;
    }

    public static <T> ListResult<T> of(Integer status, String message, List<T> data) {
        return new ListResult<>(status, message, data);
    }
}