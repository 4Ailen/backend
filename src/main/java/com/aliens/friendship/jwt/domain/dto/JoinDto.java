package com.aliens.friendship.jwt.domain.dto;

import com.aliens.friendship.nationality.domain.Nationality;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class JoinDto {
    private String email;
    private String password;
    private String name;
    private String mbti;
    private String gender;
    private Nationality nationality;
    private Integer age;
}
