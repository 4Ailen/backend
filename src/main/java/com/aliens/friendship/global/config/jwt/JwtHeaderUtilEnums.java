package com.aliens.friendship.global.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum JwtHeaderUtilEnums {

    GRANT_TYPE("Bearer ", "Bearer ");

    private String description;
    private String value;
}