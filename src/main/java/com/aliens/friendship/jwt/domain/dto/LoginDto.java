package com.aliens.friendship.jwt.domain.dto;


import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LoginDto {

    private String email;
    private String password;
}