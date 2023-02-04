package com.aliens.friendship.member.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ProfileImageService {

    //todo: 파일 기본 경로 properties로 설정
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 10; // 10 MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final String DEFAULT_FILE_PATH = "/files/";

    public String uploadProfileImage(MultipartFile uploadedFile) throws Exception {
        validateFile(uploadedFile);
        String fileName = getRandomFileName();
        uploadedFile.transferTo(new File(getUploadPath(fileName)));
        return DEFAULT_FILE_PATH + fileName;
    }

    private void validateFile(MultipartFile uploadedFile) throws Exception {
        validateNotEmpty(uploadedFile);
        validateFileSize(uploadedFile);
        validateFileExtension(uploadedFile);
    }

    private String getRandomFileName() {
        UUID uuid = UUID.randomUUID();
        return uuid + "_ProfileImage";
    }

    private String getUploadPath(String fileName) {
        return System.getProperty("user.dir") + DEFAULT_FILE_PATH + fileName;
    }

    private void validateFileSize(MultipartFile uploadedFile) throws Exception {
        if (uploadedFile.getSize() > MAX_FILE_SIZE) {
            throw new Exception("파일 크기가 10MB을 초과하였습니다.");
        }
    }

    private void validateNotEmpty(MultipartFile uploadedFile) throws Exception {
        if (uploadedFile == null || uploadedFile.isEmpty()) {
            throw new Exception("업로드된 파일이 없습니다.");
        }
    }

    private void validateFileExtension(MultipartFile uploadedFile) throws Exception {
        String extension = getFileExtension(uploadedFile.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new Exception("허용되지 않은 파일 확장자입니다. 허용되는 확장자: " + ALLOWED_EXTENSIONS);
        }
    }

    private String getFileExtension(String originalFileName) {
        int dotIndex = originalFileName.lastIndexOf('.');
        return originalFileName.substring(dotIndex + 1).toLowerCase();
    }
}