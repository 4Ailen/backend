package com.aliens.friendship.member.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class ProfileImageService {

    //todo: 파일 기본 경로 properties로 설정
    private static final String DEFAULT_FILE_PATH = "/files/";

    public String uploadProfileImage(MultipartFile uploadedFile) throws Exception {
        String fileName = getRandomFileName();
        uploadedFile.transferTo(new File(getUploadPath(fileName)));
        return DEFAULT_FILE_PATH + fileName;
    }

    private String getRandomFileName() {
        UUID uuid = UUID.randomUUID();
        return uuid + "_ProfileImage";
    }

    private String getUploadPath(String fileName) {
        return System.getProperty("user.dir") + DEFAULT_FILE_PATH + fileName;
    }
}