package com.sysbean.campusive.app.controller;

import com.sysbean.campusive.app.config.DomainUser;
import com.sysbean.campusive.app.dto.*;
import com.sysbean.campusive.app.model.Notification;
import com.sysbean.campusive.app.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/notifications")
public class NotificationController {
    private final StudentService studentService;
    private final GroupService groupService;
    private final FileService fileService;
    private final NotificationService notificationService;
    private final NotificationDefaulterService notificationDefaulterService;


    @Value("${com.sysbean.campusive.app.base-url}")
    private String baseUrl;

    Thread thread = new Thread();

    public NotificationController(StudentService studentService, GroupService groupService, FileService fileService, NotificationService notificationService, NotificationDefaulterService notificationDefaulterService) {
        this.studentService = studentService;
        this.groupService = groupService;
        this.fileService = fileService;
        this.notificationService = notificationService;
        this.notificationDefaulterService = notificationDefaulterService;
    }




    @GetMapping("/all")
    public ModelAndView getAllNotification()  {
        ModelAndView modelAndView = new ModelAndView();
        List<NotificationDTO> notifications = new ArrayList<>();
        List<NotificationDTO> notifications1 = notificationService.getAllNotificationDetail();
        for(NotificationDTO n : notifications1){
            List<NotificationDefaulterDTO> notificationDTOList = notificationDefaulterService.getAllNotificationDefaultersOfANotification(n.getNotificationId());

            if(notificationDTOList.size()>0) {
                NotificationDTO notificationDTO = notificationService.getNotificationByIdDetails(n.getNotificationId());

                notificationDTO.setStatus("Partially Successful");
                notifications.add(notificationDTO);
            }

            else{
                NotificationDTO notificationDTO = notificationService.getNotificationByIdDetails(n.getNotificationId());

                notificationDTO.setStatus("Successful");
                notifications.add(notificationDTO);
            }
        }
        List<StudentDTO> students = studentService.getAllStudentDetail();
        List<GroupsDTO> groups = groupService.getAllGroupsDetail();

        modelAndView.addObject("allGroups",groups);
        modelAndView.addObject("allStudents", students);
        modelAndView.addObject("allNotifications", notifications);
        modelAndView.addObject("notifications", new CreateNotificationDTO());
        modelAndView.setViewName("notification-1");
        return modelAndView;
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
          String contentType;
        byte[] data = fileService.downloadingFile(fileName);
          String fileExtension = fileService.getFileExtention(fileName);

            // Setting the content-type
            if(fileExtension.equals("jpg")  || fileExtension.equals("jpeg")){
                contentType = "image/jpeg";
            } else if (fileExtension.equals("pdf")) {
                contentType = "application/pdf";
            }
            else{
                contentType = "application/octet-stream";
        }


        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", contentType)
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/add")
    public ModelAndView createNotification() {
        ModelAndView modelAndView = new ModelAndView();
        List<StudentDTO> students = studentService.getAllStudentDetail();
        List<GroupsDTO> groups = groupService.getAllGroupsDetail();

        modelAndView.addObject("allGroups",groups);
        modelAndView.addObject("allStudents", students);
        modelAndView.addObject("notifications", new CreateNotificationDTO());
        modelAndView.setViewName("notification-1");

        return modelAndView;
    }

    @GetMapping("/defaulters/{id}")
    public ModelAndView GetAllDefaultersOfANotification(@PathVariable String id ) {
        ModelAndView modelAndView = new ModelAndView();
        List<NotificationDefaulterDTO> notificationDefaulters = notificationDefaulterService.getAllNotificationDefaultersOfANotification(id);
        modelAndView.addObject("notificationDefaulters",notificationDefaulters);
        modelAndView.setViewName("notification_defaulters_table");
        return modelAndView;
    }

    @GetMapping("/group/{id}")
    public ModelAndView GetAllGroupOfANotification(@PathVariable String id ) {
        ModelAndView modelAndView = new ModelAndView();
        //List<NotificationDefaulterDTO> notificationDefaulters = notificationDefaulterService.getAllGroupNameOfANotification(id);
      NotificationDTO notification= notificationService.getGroupNotificationById(id);
        System.out.println("satya :"+notification.getGroups());

        modelAndView.addObject("notifications",notification);
        modelAndView.setViewName("notification_group_table");
        return modelAndView;
    }

@GetMapping("/CheckNotificationsSent")
public ModelAndView resetPassword() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.addObject("changePassword", new ChangePasswordDTO());
    //System.out.println(passwords);
    modelAndView.setViewName("sent_notification");
    return modelAndView;
}
    @GetMapping("/delete/{notificationId}")
    public ModelAndView deleteNotification( @PathVariable String notificationId, HttpSession session){

        System.out.println("satya"+notificationId);
        notificationService.deleteNotification(notificationId);
        ModelAndView modelAndView = new ModelAndView();
        session.setAttribute("message", new MessageDTO("Notification Deleted Successfully", "Success"));
        modelAndView.addObject("message", "Notification Successfully deleted");
        modelAndView.setViewName("redirect:/app/notifications/all");

        return modelAndView;
    }

    @PostMapping("/")
    public ModelAndView createNotification(@ModelAttribute("notifications") CreateNotificationDTO createNotificationDTO, HttpSession session, @AuthenticationPrincipal DomainUser domainUser) {

        ModelAndView modelAndView = new ModelAndView();




        String fileExtension = null;
        String fileLink = null;

        if(!createNotificationDTO.getFile().isEmpty()) {

            FileDTO fileDto = fileService.uploadFile(createNotificationDTO.getFile());

            fileLink = baseUrl+"app/notifications/download/"+fileDto.getFileName();

            fileExtension = fileService.getFileExtention(fileDto.getFileName());

        }

        createNotificationDTO.setFileLink(fileLink);


        NotificationDTO notificationDto = notificationService.createNotification(createNotificationDTO,domainUser);
        session.setAttribute("message", new MessageDTO("Notification Created Successfully", "Success"));

        modelAndView.addObject("message", "Notifications Successfully Created");

        modelAndView.setViewName("redirect:/app/notifications/all");




        System.out.println("The File Extension is"+ " " + fileExtension);

        modelAndView.addObject("message", "Notifications Successfully Created");

        modelAndView.setViewName("redirect:/app/notifications/all");

        if(!createNotificationDTO.getGroupId().isEmpty()){

            List<String> phoneNumbers = new ArrayList<>();

            for(String group : createNotificationDTO.getGroupId()){

                List<StudentDTO> studentDTOList = studentService.getAllStudentsByGroup(group);

                studentDTOList.stream().map(sl -> phoneNumbers.add(sl.getPhoneNumber())).collect(Collectors.toList());
            }

            for (String ph : phoneNumbers){
                RestTemplate restTemplate = new RestTemplate();

                NotificationResponseDTO result;
                if(!createNotificationDTO.getFile().isEmpty()) {

                    if(fileExtension.equals("jpg") || fileExtension.equals("jpeg")) {

                        result = restTemplate.getForObject("http://app.promosms.in/wapp/api/send?apikey=c12964ffc6674a62b939175791cf3b45&mobile="+ph+
                                "&msg="+createNotificationDTO.getNotificationBody()+"&img1="+fileLink, NotificationResponseDTO.class);
                        System.out.println(result.getStatus());
                        System.out.println(result.getStatuscode());
                        System.out.println(result.getErrormsg());
                    }
                    else{
                        result = restTemplate.getForObject("http://app.promosms.in/wapp/api/send?apikey=c12964ffc6674a62b939175791cf3b45&mobile=" + ph +
                                "&msg=" + createNotificationDTO.getNotificationBody() +"&pdf="+ fileLink, NotificationResponseDTO.class);
                        System.out.println(result);
                    }
                }
                else{
                    result = restTemplate.getForObject("http://app.promosms.in/wapp/api/send?apikey=c12964ffc6674a62b939175791cf3b45&mobile="+ ph +
                            "&msg="+createNotificationDTO.getNotificationBody() , NotificationResponseDTO.class);

                    System.out.println(result);
                }
                if(!result.getStatus().equals("success")){
                    System.out.println("These Numbers Recieved Faliure Message");
                    System.out.println(result.getStatus() + " for this number"+ ph);
                    CreateNotificationDefaulterDTO createNotificationDefaulterDTO = new CreateNotificationDefaulterDTO(ph,notificationDto.getNotificationId(), result.getErrormsg());
                    NotificationDefaulterDTO notificationDefaulterDTO = notificationDefaulterService.createNotificationDefaulter(createNotificationDefaulterDTO);

                }
                else{
                    System.out.println("These Numbers Recieved Success Message");
                    System.out.println(result.getStatus() + " for this number"+ ph);
                }

                System.out.println("pdf file link is:" + fileLink);
            }

        }
        else{

            for (String phoneNumber : createNotificationDTO.getPhoneNumbers()){
                RestTemplate restTemplate = new RestTemplate();
                NotificationResponseDTO result;
                if(!createNotificationDTO.getFile().isEmpty()) {

                    if(fileExtension.equals("pdf")) {
                        result = restTemplate.getForObject("http://app.promosms.in/wapp/api/send?apikey=c12964ffc6674a62b939175791cf3b45&mobile=" +phoneNumber+
                                "&msg="+createNotificationDTO.getNotificationBody()+"&pdf="+fileLink, NotificationResponseDTO.class);

                    }
                    else{
                        result = restTemplate.getForObject("http://app.promosms.in/wapp/api/send?apikey=c12964ffc6674a62b939175791cf3b45&mobile="+phoneNumber+
                                "&msg="+createNotificationDTO.getNotificationBody()+"&img1="+fileLink, NotificationResponseDTO.class);

                    }
                }
                else{
                    result = restTemplate.getForObject("http://app.promosms.in/wapp/api/send?apikey=c12964ffc6674a62b939175791cf3b45&mobile="+phoneNumber +
                            "&msg=" + createNotificationDTO.getNotificationBody() , NotificationResponseDTO.class);


                }


                if(!result.getStatus().equals("success")){
                    System.out.println("These Numbers Recieved Faliure Message");
                    System.out.println(phoneNumber);

                    CreateNotificationDefaulterDTO createNotificationDefaulterDTO = new CreateNotificationDefaulterDTO(phoneNumber,notificationDto.getNotificationId(), result.getErrormsg());
                    NotificationDefaulterDTO notificationDefaulterDTO = notificationDefaulterService.createNotificationDefaulter(createNotificationDefaulterDTO);
                }
                else{
                    System.out.println("These Numbers Recieved Faliure Message");
                    System.out.println(phoneNumber);
                }

                System.out.println("pdf file link is:" + fileLink);

            }

        }

        return modelAndView;

    }
}
