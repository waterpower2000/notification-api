package com.sysbean.campusive.app.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.sysbean.campusive.app.dto.FileDTO;
import com.sysbean.campusive.app.model.FileEntity;
import com.sysbean.campusive.app.repository.FileEntityRepository;
import com.sysbean.campusive.app.util.CommonUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final FileEntityRepository fileEntityRepository;
    private final AmazonS3 amazonS3;

    @Value("${com.sysbean.campusive.app.aws.bucket-name}")
    private String bucketName;

    public FileServiceImpl(FileEntityRepository fileEntityRepository, AmazonS3 amazonS3) {
        this.fileEntityRepository = fileEntityRepository;
        this.amazonS3 = amazonS3;
    }

    @Override
    public FileDTO uploadFile(MultipartFile multipart) {
        File convertedFile = null;
        try {
            convertedFile = CommonUtil.convertMultiPartToFile(multipart);
            String fileExtension = FilenameUtils.getExtension(convertedFile.getAbsolutePath());
            String originalFileName = convertedFile.getName();
            String awsFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            long size = convertedFile.length();

            amazonS3.putObject(bucketName, awsFileName, convertedFile);
            FileEntity fileEntity = new FileEntity(awsFileName, originalFileName, fileExtension, size);
            fileEntity = fileEntityRepository.save(fileEntity);
            return convertIntoDTO(fileEntity);
        } finally {
            if(convertedFile != null){
                convertedFile.delete();
            }
        }
    }

    @Override
    public FileEntity getFile(String fileCode) {
        return fileEntityRepository.findById(fileCode).orElse(null);
    }

    @Override
    public String getFileExtention(String fileName){
        int index = fileName.lastIndexOf('.');

        String extension = fileName.substring(index + 1);
        return extension;
    }

    @Override
    public byte[] getFileContent(String fileCode) {
        File file = null;
        try {
            file = downloadFile(fileCode);
            return IOUtils.toByteArray(Files.newInputStream(file.toPath()));
        } catch (Exception e) {
            throw new RuntimeException("File not found");
        } finally {
            if(file != null){
                file.delete();
            }
        }
    }

    @Override
    public byte[] downloadingFile(String fileName) {
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File downloadFile(String fileCode) {
        FileEntity fileEntity = getFile(fileCode);
        if(fileEntity == null) {
            return null;
        }
        String fileName = fileEntity.getFileName();
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);

        try {
            File tmp = File.createTempFile(fileEntity.getFileOriginalName(), fileEntity.getFileExtension());
            Files.copy(s3Object.getObjectContent(), tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return tmp;
        } catch (Exception e) {
            return null;
        }
    }

    private FileDTO convertIntoDTO(FileEntity fileEntity) {
        if(fileEntity == null) {
            return null;
        }

        return new FileDTO(fileEntity.getFileCode(), fileEntity.getFileName(), fileEntity.getFileOriginalName(), fileEntity.getFileSize());
    }
}

