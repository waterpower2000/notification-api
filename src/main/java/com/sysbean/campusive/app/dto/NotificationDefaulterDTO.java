package com.sysbean.campusive.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDefaulterDTO {
    private String notificationDefaulterId;
    private String notificationId;
    private String studentId;
    private String studentName;
    private String ClassName;
    private String phoneNumber;
    private String errorMessage;
}
