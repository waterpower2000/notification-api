package com.sysbean.campusive.app.repository;

import com.sysbean.campusive.app.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileEntityRepository extends JpaRepository<FileEntity, String> {
}
