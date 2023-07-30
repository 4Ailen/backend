package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.friendship.domain.member.validation.ProfileImageValidate;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ProfileImageRequestDto {
    @ProfileImageValidate
    private final MultipartFile profileImage;

    public ProfileImageRequestDto(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }
}