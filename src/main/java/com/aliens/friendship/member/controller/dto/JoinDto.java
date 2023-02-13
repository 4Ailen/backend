package com.aliens.friendship.member.controller.dto;

import com.aliens.friendship.member.domain.Nationality;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class JoinDto {
    private String email;
    private String password;
    private String name;
    private String mbti;
    private String gender;
    private Nationality nationality;
    private String birthday;
    private MultipartFile image;
    @Builder.Default
    private String imageUrl = "default.png";
}
