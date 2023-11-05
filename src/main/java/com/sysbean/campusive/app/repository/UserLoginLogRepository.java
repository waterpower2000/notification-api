package com.sysbean.campusive.app.repository;


import com.sysbean.campusive.app.model.UserLoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, String> {

}
