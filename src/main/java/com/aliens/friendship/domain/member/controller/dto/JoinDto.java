package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.domain.Nationality;
import com.aliens.friendship.domain.member.validation.ProfileImageValidate;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class JoinDto {
    private String email;
    private String password;
    private String name;
    private Member.Mbti mbti;
    private String gender;
    private Nationality nationality;
    private String birthday;
    @ProfileImageValidate
    private MultipartFile profileImage;
    @Builder.Default
    private String imageUrl = "default.png";
}