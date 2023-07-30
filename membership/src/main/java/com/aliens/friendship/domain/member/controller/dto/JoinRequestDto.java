package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.member.validation.ProfileImageValidate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class JoinRequestDto {
    private String email;
    private String password;
    private String name;
    private MemberEntity.Mbti mbti;
    private String gender;
    private String nationality;
    private String birthday;
    private String selfIntroduction;
    @ProfileImageValidate
    @JsonIgnore
    private MultipartFile profileImage;
    @Builder.Default
    private String imageUrl = "default.png";
}