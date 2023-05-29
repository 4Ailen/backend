package com.aliens.friendship.domain.auth.dto.request;

import lombok.Getter;

@Getter
public class LoginRequest {

    private final String email;
    private final String password;

    private LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static LoginRequest of(
            String email,
            String password
    ) {
        return new LoginRequest(email, password);
    }
}