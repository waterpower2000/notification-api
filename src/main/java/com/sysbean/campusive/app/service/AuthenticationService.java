package com.sysbean.campusive.app.service;


import com.sysbean.campusive.app.config.DomainUser;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    DomainUser authenticate(String userCode, String userPass, HttpServletRequest httpServletRequest);
}
