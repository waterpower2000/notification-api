package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.dto.CreateNotificationDefaulterDTO;
import com.sysbean.campusive.app.dto.NotificationDefaulterDTO;
import com.sysbean.campusive.app.model.NotificationDefaulter;

import java.util.List;

public interface NotificationDefaulterService {

    NotificationDefaulterDTO createNotificationDefaulter(CreateNotificationDefaulterDTO createNotificationDefaulterDTO);

    List<NotificationDefaulterDTO> getAllNotificationDefaultersOfANotification(String NotificationId);

    List<NotificationDefaulter> getStudentNotificationDetaulter(String studentId);

    void deleteNotificationDefaulterStudent(List<String> notificationDefaulterId);

    NotificationDefaulterDTO convertIntoDTO(NotificationDefaulter notificationDefaulter);
}
