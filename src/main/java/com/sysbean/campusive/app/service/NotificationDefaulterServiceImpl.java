package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.dto.CreateNotificationDefaulterDTO;
import com.sysbean.campusive.app.dto.NotificationDefaulterDTO;
import com.sysbean.campusive.app.model.Notification;
import com.sysbean.campusive.app.model.NotificationDefaulter;
import com.sysbean.campusive.app.model.Students;
import com.sysbean.campusive.app.repository.NotificationDefaulterRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationDefaulterServiceImpl implements NotificationDefaulterService {
    private final NotificationDefaulterRepository notificationDefaulterRepository;
    private final NotificationService notificationService;

    private final StudentService studentService;
    ;

    public NotificationDefaulterServiceImpl(NotificationDefaulterRepository notificationDefaulterRepository, NotificationService notificationService, @Lazy StudentService studentService) {
        this.notificationDefaulterRepository = notificationDefaulterRepository;
        this.notificationService = notificationService;
        this.studentService = studentService;

    }

    @Override
    public NotificationDefaulterDTO createNotificationDefaulter(CreateNotificationDefaulterDTO createNotificationDefaulterDTO) {
        Notification notification = notificationService.getNotificationById(createNotificationDefaulterDTO.getNotificationId());
        Students students = studentService.getStudentByPhoneNumber(createNotificationDefaulterDTO.getPhoneNumber());

        NotificationDefaulter notificationDefaulter = new NotificationDefaulter(students, notification, createNotificationDefaulterDTO.getErrorMessage());

        return convertIntoDTO(notificationDefaulterRepository.save(notificationDefaulter));
    }

    @Override
    public List<NotificationDefaulterDTO> getAllNotificationDefaultersOfANotification(String NotificationId) {
        Notification notification = notificationService.getNotificationById(NotificationId);
        List<NotificationDefaulter> notificationDefaulters = notificationDefaulterRepository.findByNotification(notification);
        return notificationDefaulters.stream().map(this::convertIntoDTO).collect(Collectors.toList());
    }

    @Override
    public List<NotificationDefaulter> getStudentNotificationDetaulter(String studentId) {
        Students students = studentService.getStudentById(studentId);
        List<NotificationDefaulter> notificationDefaulter = notificationDefaulterRepository.findByStudent(students);
        return notificationDefaulter;
    }


    private NotificationDefaulter getNotificationDefaulter(String notificationDefaulterId) {
        Optional<NotificationDefaulter> notificationDefaulterOptional = notificationDefaulterRepository.findById(notificationDefaulterId);
        if(!notificationDefaulterOptional.isPresent()){
            throw new RuntimeException("No Defaulter Found For this id");
        }
        return notificationDefaulterOptional.get();
    }

    @Override
    public void deleteNotificationDefaulterStudent(List<String> notificationDefaulterId) {


        for (String id:notificationDefaulterId) {
            NotificationDefaulter notificationDefaulter = getNotificationDefaulter(id);
            notificationDefaulterRepository.delete(notificationDefaulter);
        }



    }




    @Override
    public NotificationDefaulterDTO convertIntoDTO(NotificationDefaulter notificationDefaulter){
        Students students = studentService.getStudentById(notificationDefaulter.getStudent().getStudentId());

        if(notificationDefaulter == null){
            return null;
        }

        return new NotificationDefaulterDTO(notificationDefaulter.getNotificationDefaulterId(),
                notificationDefaulter.getNotification().getNotificationId(), students.getStudentId(),
                students.getStudentName(), students.getGroup().getGroupName(), students.getPhoneNumber(),notificationDefaulter.getErrorMessage());
    }
}
