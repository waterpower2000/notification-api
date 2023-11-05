package com.sysbean.campusive.app.service;


import com.sysbean.campusive.app.config.DomainUser;
import com.sysbean.campusive.app.dto.ChangePasswordDTO;
import com.sysbean.campusive.app.model.UserEntity;

public interface UserService {
    UserEntity getUserEntity( String userCode);
    boolean changePassword(ChangePasswordDTO changePassword, DomainUser domainUser);
}
