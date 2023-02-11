package com.aliens.friendship.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProfileImageServiceTest {

    @InjectMocks
    private ProfileImageService profileImageService;

    @Test
    @DisplayName("프로필 이미지 저장 성공")
    public void UploadProfileImage_Success_When_GivenValidImageFile() throws Exception {
        // given: jpg 파일 생성
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        // when: 파일 업로드
        String uploadedFilePath = profileImageService.uploadProfileImage(mockMultipartFile);

        // then: 파일이 업로드 되었는지 확인
        assertNotNull(uploadedFilePath);
        File uploadedFile = new File(System.getProperty("user.dir") + uploadedFilePath);
        assertTrue(uploadedFile.exists());
        uploadedFile.delete();
    }

    @Test
    @DisplayName("프로필 이미지 저장 예외: 파일확장자가 지원되지 않을 경우")
    public void UploadProfileImage_ThrowException_When_GivenInvalidFileExtension() throws Exception {
        // given: 텍스트 파일 생성
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test data".getBytes());

        // when: 파일 업로드
        Exception exception = assertThrows(Exception.class, () -> {
            profileImageService.uploadProfileImage(mockMultipartFile);
        });

        // then: 에러 발생
        assertTrue(exception.getMessage().contains("허용되지 않은 파일 확장자입니다."));
    }

    @Test
    @DisplayName("프로필 이미지 저장 예외: 파일 크기가 10MB를 초과할 경우")
    public void UploadProfileImage_ThrowException_When_GivenFileSizeExceeds10MB() {
        // given : 10MB를 초과하는 파일 생성
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[1024 * 1024 * 10 + 1]);

        // when: 파일 업로드
        Exception exception = assertThrows(Exception.class, () -> {
            profileImageService.uploadProfileImage(mockMultipartFile);
        });

        // then: 에러 발생
        assertEquals("파일 크기가 10MB을 초과하였습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("프로필 이미지 저장 예외: 업로드된 파일이 없을 경우")
    public void UploadProfileImage_ThrowException_When_GivenNullFile() {
        // given : null 파일 생성
        MultipartFile mockMultipartFile = null;

        // when: 파일 업로드
        Exception exception = assertThrows(Exception.class, () -> {
            profileImageService.uploadProfileImage(mockMultipartFile);
        });

        // then: 에러 발생
        assertEquals("업로드된 파일이 없습니다.", exception.getMessage());
    }
}