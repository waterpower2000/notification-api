package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.config.DomainUser;
import com.sysbean.campusive.app.dto.CreateNotificationDTO;
import com.sysbean.campusive.app.dto.GroupsDTO;
import com.sysbean.campusive.app.dto.NotificationDTO;
import com.sysbean.campusive.app.model.Notification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotification();
    List<NotificationDTO> getAllNotificationDetail();

    Notification getNotificationById(String notificationId);

    NotificationDTO getNotificationByIdDetails(String notificationId);

    NotificationDTO createNotification(CreateNotificationDTO createNotificationDTO, DomainUser domainUser);

    List<NotificationDTO> createBulkNotification(CreateNotificationDTO createNotificationDTO);

    NotificationDTO updateNotification(String notificationId, CreateNotificationDTO createNotificationDTO);

    void deleteNotification(String notificationId);

    NotificationDTO convertIntoDTO(Notification notification);

    NotificationDTO getGroupNotificationById(String id);

    //Notification getAllGroupNameOfANotification(String id);
}
