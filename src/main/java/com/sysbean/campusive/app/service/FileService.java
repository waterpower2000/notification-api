package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.dto.FileDTO;
import com.sysbean.campusive.app.model.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileService {
    FileEntity getFile(String fileCode);

    String getFileExtention(String fileName);

    byte[] getFileContent(String fileCode);

    byte[] downloadingFile(String fileName);
    File downloadFile(String fileCode);
    FileDTO uploadFile(MultipartFile multipart);
}
