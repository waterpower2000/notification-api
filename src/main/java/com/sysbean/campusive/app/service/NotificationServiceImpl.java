package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.config.DomainUser;
import com.sysbean.campusive.app.dto.CreateNotificationDTO;
import com.sysbean.campusive.app.dto.NotificationDTO;
import com.sysbean.campusive.app.dto.NotificationDefaulterDTO;
import com.sysbean.campusive.app.model.*;
import com.sysbean.campusive.app.repository.NotificationRepository;
import com.sysbean.campusive.app.repository.StudentsRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;
    private final StudentsRepository studentsRepository;
    private final GroupService groupService;

    private final NotificationDefaulterService notificationDefaulterService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, StudentsRepository studentsRepository, GroupService groupService,@Lazy NotificationDefaulterService notificationDefaulterService) {
        this.notificationRepository = notificationRepository;
        this.studentsRepository = studentsRepository;
        this.groupService = groupService;
        this.notificationDefaulterService=notificationDefaulterService;
    }



    @Override
    public List<Notification> getAllNotification() {
        return notificationRepository.findAll();
    }

    @Override
    public List<NotificationDTO> getAllNotificationDetail() {
        return getAllNotification().stream().map(this::convertIntoDTO).collect(Collectors.toList());
    }

    @Override
    public Notification getNotificationById(String notificationId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if(!notificationOptional.isPresent()){
            throw new RuntimeException("no notification found with this id");
        }

        return notificationOptional.get();
    }

    @Override
    public NotificationDTO getNotificationByIdDetails(String notificationId) {
        return convertIntoDTO(getNotificationById(notificationId));
    }

    @Override
    public NotificationDTO createNotification(CreateNotificationDTO createNotificationDTO, DomainUser domainUser) {
        //Group group = null;
        Notification notification  =null;
        List<String> groupNames = new ArrayList<>();

        if( !createNotificationDTO.getGroupId().isEmpty()){

            for (String g : createNotificationDTO.getGroupId()) {
                Group group = groupService.getGroupsById(g);
                groupNames.add(group.getGroupName());
            }


            String groups = String.join(",", groupNames);
            //createBulkNotification(createNotificationDTO);
            notification = new Notification(createNotificationDTO.getNotificationTitle(), NotificationType.valueOf(createNotificationDTO.getNotificationType()),
                    groups , null, createNotificationDTO.getFileLink(), createNotificationDTO.getNotificationBody(),domainUser.getFirstName()+domainUser.getLastName());

            notificationRepository.save(notification);

        }
        else {
             //group = groupService.getGroupsById(createNotificationDTO.getGroupId());
            String phoneNumbers = String.join(",", createNotificationDTO.getPhoneNumbers());
             notification = new Notification(createNotificationDTO.getNotificationTitle(), NotificationType.valueOf(createNotificationDTO.getNotificationType()),
                    null, phoneNumbers, createNotificationDTO.getFileLink(), createNotificationDTO.getNotificationBody(),domainUser.getFirstName()+domainUser.getLastName());

             notificationRepository.save(notification);
        }
        return convertIntoDTO(notification);
    }

    @Override
    public List<NotificationDTO> createBulkNotification(CreateNotificationDTO createNotificationDTO) {

//
//        Notification notification  =null;
//        List<Notification> notificationList = new ArrayList<>();
//
//        for (String g : createNotificationDTO.getGroupId()) {
//           Group group = groupService.getGroupsById(g);
//            System.out.println("Creating the Notification for" + group.getGroupName());
//            List<Students> studentsList = studentsRepository.findByGroup(group);
//            List<String> phoneNumberList = studentsList.stream().map(sl -> sl.getPhoneNumber()).collect(Collectors.toList());
//            String phoneNumbers = String.join(",", phoneNumberList);
//
//            notification = new Notification(createNotificationDTO.getNotificationTitle(), NotificationType.valueOf(createNotificationDTO.getNotificationType()),
//                    group, phoneNumbers, createNotificationDTO.getNotificationBody());
//            notificationList.add(notification);
//        }
//
//
//        return notificationList.stream().map(nl -> convertIntoDTO(notificationRepository.save(nl))).collect(Collectors.toList());

        return null;
    }

    @Override
    public NotificationDTO updateNotification(String notificationId, CreateNotificationDTO createNotificationDTO) {
        return null;
    }

    @Override
    public void deleteNotification(String notificationId) {
        System.out.println("deleteNotification 1");
        final List<NotificationDefaulterDTO> allNotificationDefaultersOfANotification = notificationDefaulterService.getAllNotificationDefaultersOfANotification(notificationId);
        final List<String> notificationDefaulterId = allNotificationDefaultersOfANotification.stream().map(m -> m.getNotificationDefaulterId()).collect(Collectors.toList());
        System.out.println("deleteNotification 3");
//      //  notificationDefaulterService.getStudentNotificationDetaulter(notificationId);
        if(allNotificationDefaultersOfANotification != null){
       notificationDefaulterService.deleteNotificationDefaulterStudent(notificationDefaulterId);
        }
      Notification existingNotification = getNotificationById(notificationId);
      notificationRepository.delete(existingNotification);
    }


    @Override
    public NotificationDTO convertIntoDTO(Notification notification) {
        if(notification == null){
            return null;
        }

        return new NotificationDTO(notification.getNotificationId(), notification.getNotificationTitle(), notification.getNotificationType().name(),
                notification.getGroups(), notification.getStudents(), null , notification.getAttachmentLink(), notification.getNotificationBody(), notification.getCreatedOn().toString(),notification.getCreatedBy());
    }

    @Override
    public NotificationDTO getGroupNotificationById(String id) {
        return convertIntoDTO(getNotificationById(id));
    }

//    @Override
//    public Notification getAllGroupNameOfANotification(String id) {
//        Optional<Notification> Notification = notificationRepository.findById(id);
//
//        return Notification.get();;
//    }
}
