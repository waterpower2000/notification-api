package com.sysbean.campusive.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String notificationId;
    private String notificationTitle;
    private String notificationType;
    private String groups;

    private String phoneNumbers;

    private String status;
    private String fileLink;

    private String notificationBody;

    private String createdOn;
    private String createdBy;

}
