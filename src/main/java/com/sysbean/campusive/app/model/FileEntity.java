package com.sysbean.campusive.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "TB_FILE_UPLOAD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {

    @Id
    @GenericGenerator(name = "Application-Generic-Generator", strategy = "com.sysbean.campusive.app.config.ApplicationIdentityGenerator")
    @GeneratedValue(generator = "Application-Generic-Generator")
    @Column(name = "FILE_CODE", nullable = false, unique = true)
    private String fileCode;

    @Column(name = "FILE_NAME", nullable = false, unique = true)
    private String fileName;

    @Column(name = "FILE_ORIGINAL_NAME", nullable = false)
    private String fileOriginalName;

    @Column(name = "FILE_SIZE")
    private long fileSize;

    @Column(name = "FILE_EXTENSION")
    private String fileExtension;

    public FileEntity(String fileName, String fileOriginalName, String fileExtension, long fileSize) {
        this.fileName = fileName;
        this.fileOriginalName = fileOriginalName;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
    }
}
