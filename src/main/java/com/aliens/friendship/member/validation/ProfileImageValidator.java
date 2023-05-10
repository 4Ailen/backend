package com.aliens.friendship.member.validation;

import com.aliens.friendship.global.util.ImageUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ProfileImageValidator
        implements ConstraintValidator<ProfileImageValidate, MultipartFile> {

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png");
    private static final int MAX_IMAGE_FILE_SIZE = 1024 * 1024 * 10; // 10 MB

    @Override
    public boolean isValid(
            MultipartFile requestImage,
            ConstraintValidatorContext context
    ) {

        // 이미지 확장자 검증
        if (requestImage != null && !validateImageExtension(ImageUtil.getImageExtension(requestImage.getOriginalFilename()))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("회원 프로필 이미지는 " + ALLOWED_IMAGE_EXTENSIONS + " 확장자만 가능합니다.")
                    .addConstraintViolation();
            return false;
        }

        // 이미지 크기 검증
        if (requestImage != null && !validateImageSize(requestImage.getSize())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("회원 프로필 이미지는 10MB 이하여야 합니다.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    @Override
    public void initialize(ProfileImageValidate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    // 이미지의 확장자가 유효한지 검증
    private boolean validateImageExtension(String imageExtension) {
        return ALLOWED_IMAGE_EXTENSIONS.contains(imageExtension);
    }

    // 요청된 이미지의 크기가 유효한 크기인지 검증
    private boolean validateImageSize(long imageSize) {
        return imageSize <= MAX_IMAGE_FILE_SIZE;
    }
}