package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.config.DomainUser;
import com.sysbean.campusive.app.dto.ChangePasswordDTO;
import com.sysbean.campusive.app.model.UserEntity;
import com.sysbean.campusive.app.repository.UserEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Lazy;



import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserEntityRepository userEntityRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity getUserEntity(String userCode) {
        Optional<UserEntity> optionalUserEntity = userEntityRepository.findById(userCode);
        return optionalUserEntity.orElse(null);
    }

    @Override
    public boolean changePassword(ChangePasswordDTO changePassword, DomainUser domainUser) {
//		System.out.println(changePassword.getPassword() + " " + changePassword.getConfirmPassword());
//		System.out.println(domainUser+" null value line 37");
        UserEntity userEntity = getUserEntity(changePassword.getUserCode());
        //System.out.println(domainUser.getUsername()+" "+domainUser.getPassword()+" "+domainUser.getFirstName());
        if (changePassword.getNewPassword() == null || changePassword.getNewPassword().length() < 8) {
            System.out.println("null value");
            return false;
        }

        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            System.out.println("not equal");
            return false;
        }
        System.out.println("satyapraksh swain");
        if (userEntity == null) {
            System.out.println("optional class");
            return false;
        }
        System.out.println("error");
        userEntity.setUserPass(passwordEncoder.encode(changePassword.getNewPassword()));
        UserEntity save = userEntityRepository.save(userEntity);
        System.out.println("save"+save);
        return true;
    }
}
