package com.aliens.friendship.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 성공 결과만 반환하는 응답
 */
@Getter
@AllArgsConstructor
public class CommonResult {
    private Integer status;
    private String message;
}
