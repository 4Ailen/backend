package com.aliens.friendship.member.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class FileUploadService {

    public void uploadFile(String fileName, MultipartFile uploadedFile) throws Exception {
        String uploadPath = System.getProperty("user.dir") + "/files/";
        File addedFile = new File(uploadPath + fileName);
        uploadedFile.transferTo(addedFile);
    }

}