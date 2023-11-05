package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.dto.BiDTO;
import com.sysbean.campusive.app.dto.StudentDTO;
import com.sysbean.campusive.app.model.NotificationUser;
import com.sysbean.campusive.app.model.Students;

import java.util.List;

public interface NotificationUserService {
//    NotificationUser createNotificationUser(NotificationUser notificationUser);
//


    BiDTO createNotificationUser(String fromNumber, String toNumber, String message);

    BiDTO convertIntoDTO(NotificationUser notificationUser);

    //NotificationUser getNotificationUserByMessage(String fromNumber,String  toNumber,String message);
    NotificationUser getNotificationUserByMessage(String Id);
}
