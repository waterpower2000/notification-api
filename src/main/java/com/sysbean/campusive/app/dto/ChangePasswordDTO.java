package com.sysbean.campusive.app.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {
    private String userCode;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
