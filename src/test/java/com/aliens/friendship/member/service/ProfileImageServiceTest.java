package com.aliens.friendship.member.service;

import com.aliens.friendship.domain.member.service.ProfileImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProfileImageServiceTest {

    @InjectMocks
    private ProfileImageService profileImageService;

    @Test
    @DisplayName("프로필 이미지 저장 성공")
    public void UploadProfileImage_Success_When_GivenValidImageFile() throws Exception {
        // given: png 파일 생성
        MockMultipartFile mockMultipartFile = createMockImage();

        // when: 파일 업로드
        String uploadedFilePath = profileImageService.uploadProfileImage(mockMultipartFile);

        // then: 파일이 업로드 되었는지 확인R
        assertNotNull(uploadedFilePath);
        File uploadedFile = new File(System.getProperty("user.dir") + uploadedFilePath);
        assertTrue(uploadedFile.exists());
        uploadedFile.delete();
    }

    // Mock 회원 프로필 이미지 생성
    private static MockMultipartFile createMockImage()
            throws IOException {
        final String fileName = "test"; //파일명
        final String contentType = "png"; //파일타입
        final String filePath = "src/test/resources/testImage/" + fileName + "." + contentType; //파일경로
        FileInputStream fileInputStream = new FileInputStream(filePath);
        return new MockMultipartFile(
                "profileImage",
                fileName + "." + contentType,
                "image/png",
                fileInputStream
        );
    }
}

