package com.aliens.friendship.domain.jwt.domain.dto;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MemberInfo {

    private String email;
    private String username;
}