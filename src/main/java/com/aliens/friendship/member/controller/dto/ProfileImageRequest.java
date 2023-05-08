package com.aliens.friendship.member.controller.dto;

import com.aliens.friendship.member.validation.ProfileImageValidate;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ProfileImageRequest {
    @ProfileImageValidate
    private final MultipartFile profileImage;

    public ProfileImageRequest(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }
}