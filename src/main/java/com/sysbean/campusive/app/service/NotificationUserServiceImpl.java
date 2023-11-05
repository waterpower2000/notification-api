package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.dto.BiDTO;
import com.sysbean.campusive.app.dto.CreateStudentDTO;
import com.sysbean.campusive.app.dto.NotificationResponseDTO;
import com.sysbean.campusive.app.dto.StudentDTO;
import com.sysbean.campusive.app.model.Group;
import com.sysbean.campusive.app.model.NotificationUser;
import com.sysbean.campusive.app.model.Students;
import com.sysbean.campusive.app.repository.NotificationUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationUserServiceImpl implements NotificationUserService {
    private NotificationUserRepository notificationUserRepository;
    
    @Autowired
    private StudentService studentService;
    @Autowired
    private NotificationService notificationService;

    public NotificationUserServiceImpl(NotificationUserRepository notificationUserRepository) {
        this.notificationUserRepository = notificationUserRepository;
    }

//    @Override
//    public NotificationUser createNotificationUser(NotificationUser notificationUser) {
//        String randdomUserId = UUID.randomUUID().toString();
//        LocalDate currentDate = LocalDate.now();
//        notificationUser.setId(randdomUserId);
//        notificationUser.setCreatedOn(currentDate);
//        return notificationUserRepository.save(notificationUser);
//    }
//
//    @Override
//    public NotificationUser getNotificationUserByMessage(String fromNumber,String toNumber,String message) {
//        return notificationUserRepository.findByProperties(fromNumber, toNumber, message);
//    }
@Override
   public NotificationUser getNotificationUserByMessage(String Id) {
        return notificationUserRepository.findById(Id).get();
   }

    @Override
    public BiDTO createNotificationUser(String fromNumber, String toNumber, String message) {
        LocalDate currentDate = LocalDate.now();
        NotificationUser notificationUser=new NotificationUser(UUID.randomUUID().toString(),fromNumber,toNumber,message,currentDate);

//        return new notificationUserRepository.save(notificationUser);
        return convertIntoDTO(notificationUserRepository.save(notificationUser));
    }
    @Override
    public BiDTO convertIntoDTO(NotificationUser notificationUser) {
        if(notificationUser== null) {
            return null;
        }
        return new BiDTO(notificationUser.getId(), notificationUser.getFromNumber(), notificationUser.getToNumber(), notificationUser.getMessage());
    }

    
    
  //Scheduler Job
    @Scheduled(cron = "0 0 7 * * ? ") // Runs daily at 07:00 AM
    public void sendScheduledBirthdayWishes() {
        try {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<Students> allStudents = studentService.getAllStudents();
            for (Students students : allStudents) {
                if(students.getStudentDob() != null){
                    LocalDate birthday = LocalDate.parse(students.getStudentDob(), formatter);
                    if (isSameMonthAndDay(birthday, today)) {
                        sendBirthdayWishes(students);
                    }
                }
            }
        }
        catch( Exception e)
        {
             e.printStackTrace();
        }
    }
    private static boolean isSameMonthAndDay(LocalDate birthday, LocalDate today) {
        return birthday.getMonth() == today.getMonth() && birthday.getDayOfMonth() == today.getDayOfMonth();
    }
    private void sendBirthdayWishes(Students students) {
        Students studentById = studentService.getStudentById(students.getStudentId());
        String phoneNumber = studentById.getPhoneNumber();
        RestTemplate restTemplate = new RestTemplate();
        NotificationResponseDTO forObject = restTemplate
                .getForObject("http://app.promosms.in/wapp/api/send?apikey=c12964ffc6674a62b939175791cf3b45&mobile=" + phoneNumber +
                                "&msg=" + "Happy BirthDay " + students.getStudentName() + " Many Many Happy Returns of the day! On Behalf of Lion's School! Enjoy your Day!"
                        ,NotificationResponseDTO.class);
        System.out.println("Birthday wish sent");
    }
    @Scheduled(cron = "0 0 17 * * ? ") // Runs daily at 5:00 PM
    public void sendScheduledBirthdayWishToOthers() {
        try{
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Students> allStudents = studentService.getAllStudents();
        List<String> phoneNumbers = new ArrayList<>();
        String studentName="";
        List<Students> allStudentGroup = new ArrayList<>();
        LocalDate birthday = null;
            for (Students student : allStudents) {
                if (student.getStudentDob() != null) {
                    birthday = LocalDate.parse(student.getStudentDob(), formatter);
                    if (!isSameOrnotMonthAndDay(birthday, tomorrow)) {
                        studentName = student.getStudentName();
                        Group group = student.getGroup();
                        allStudentGroup = studentService.getAllStudentGroup(group.getGroupId());
                    }
                }
            }
                for (Students students : allStudentGroup) {
                	birthday = LocalDate.parse(students.getStudentDob(), formatter);
                    if (isSameOrnotMonthAndDay(birthday, tomorrow)) {
                        phoneNumbers.add(students.getPhoneNumber());
                    }
                }
                if(!phoneNumbers.isEmpty()){
                    sendNotificationToOthers(phoneNumbers,studentName);
                }
            } catch(Exception e){
            e.printStackTrace();
        }
    }
    private void sendNotificationToOthers(List<String> phoneNumbers,String studentName) {
        for (String phoneNumber : phoneNumbers) {
            RestTemplate restTemplate = new RestTemplate();
            NotificationResponseDTO forObject = restTemplate
                    .getForObject("http://app.promosms.in/wapp/api/send?apikey=c12964ffc6674a62b939175791cf3b45&mobile=" + phoneNumber +
                            "&msg=" +"Tomorrow we will celebrate "+ studentName+ "'s BirthDay!",NotificationResponseDTO.class);
            System.out.println("Birthday Notification send");
        }
    }
    private static boolean isSameOrnotMonthAndDay(LocalDate birthday, LocalDate tomorrow) {
        return !(birthday.getMonth() == tomorrow.getMonth() && birthday.getDayOfMonth() == tomorrow.getDayOfMonth());
    }

}
