package com.sysbean.campusive.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNotificationDTO {

    private String notificationTitle;

    private String notificationType;

    private String groups;
    private String fileLink;

    private List<String> groupId;

    private List<String>phoneNumbers;

    private String notificationBody;
    private MultipartFile file;
}
