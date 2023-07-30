package com.aliens.friendship.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

import static com.aliens.friendship.global.util.ImageUtil.getImageExtension;

@Service
public class ProfileImageService {

    private static final String DEFAULT_FILE_PATH = "/files/";

    private static final String DEFAULT_PROFILE_IMAGE_PATH = "/files/default_profile_image.png";

    public String uploadProfileImage(MultipartFile uploadedFile) throws Exception {
        if (uploadedFile == null || uploadedFile.isEmpty()) {
            return DEFAULT_PROFILE_IMAGE_PATH;
        }
        String fileName = getRandomFileName();
        uploadedFile.transferTo(new File(getUploadPath(fileName + "." + getImageExtension(uploadedFile.getOriginalFilename()))));
        return DEFAULT_FILE_PATH + fileName + "." + getImageExtension(uploadedFile.getOriginalFilename());
    }

    public boolean deleteProfileImage(String filePath) {
        File file = new File(System.getProperty("user.dir") + filePath);
        return file.delete();
    }

    private String getRandomFileName() {
        UUID uuid = UUID.randomUUID();
        return uuid + "_ProfileImage";
    }

    // TODO: 해당 메서드도 ImageUtil 에 추가할 수 있지 않을까?
    private String getUploadPath(String fileName) {
        return System.getProperty("user.dir") + DEFAULT_FILE_PATH + fileName;
    }
}