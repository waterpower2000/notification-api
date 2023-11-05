package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.config.DomainUser;
import com.sysbean.campusive.app.model.UserEntity;
import com.sysbean.campusive.app.model.UserLoginLog;
import com.sysbean.campusive.app.repository.UserLoginLogRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserLoginLogRepository loginLogRepository;

    public AuthenticationServiceImpl(UserService userService, @Lazy PasswordEncoder passwordEncoder, UserLoginLogRepository loginLogRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.loginLogRepository = loginLogRepository;
    }

    @Override
    public DomainUser authenticate(String userCode, String userPass, HttpServletRequest httpServletRequest) {
        UserEntity userEntity = userService.getUserEntity(userCode);


        if(userEntity == null) {
            throw new BadCredentialsException("Invalid User.");
        }

        if(!userEntity.isEnabled()) {
            throw new BadCredentialsException("Login Disabled");
        }

        boolean matches = passwordEncoder.matches(userPass, userEntity.getUserPass());

        if(!matches) {
            throw new BadCredentialsException("Login Failed");
        }

        String userAgent = httpServletRequest.getHeader("User-Agent");
        String terminal = httpServletRequest.getRemoteAddr();

        UserLoginLog userLoginLog = new UserLoginLog();
        userLoginLog.setUserEntity(userEntity);
        userLoginLog.setLoginTime(LocalDateTime.now());
        userLoginLog.setLoginTerminal(terminal);
        userLoginLog.setUserAgent(userAgent);
        userLoginLog = loginLogRepository.saveAndFlush(userLoginLog);

        DomainUser domainUser = new DomainUser(userEntity.getUserCode(), userEntity.getUserPass(), userEntity.isEnabled(), Collections.singletonList("ROLE_ADMIN"));
        domainUser.setFirstName(userEntity.getFirstName());
        domainUser.setLastName(userEntity.getLastName());
        domainUser.setLoginId(userLoginLog.getLoginId());
        domainUser.setLoginTime(userLoginLog.getLoginTime());
        return domainUser;
    }
}
