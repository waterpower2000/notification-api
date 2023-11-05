package com.sysbean.campusive.app.repository;

import com.sysbean.campusive.app.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, String> {

}
