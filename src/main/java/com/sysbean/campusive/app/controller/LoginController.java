package com.sysbean.campusive.app.controller;

import com.sysbean.campusive.app.model.NotificationUser;
import com.sysbean.campusive.app.service.NotificationUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.sysbean.campusive.app.config.DomainUser;
import com.sysbean.campusive.app.dto.*;
import com.sysbean.campusive.app.service.UserService;

@Controller
public class LoginController {

    private final UserService userService;
    private  final NotificationUserService notificationUserService;

    public LoginController(UserService userService, NotificationUserService notificationUserService) {
        this.userService = userService;
        this.notificationUserService = notificationUserService;
    }

//    @GetMapping("/receieve/message")
//    public ResponseEntity<BiDTO> recieveMessage(){
//        BiDTO biDTO = new BiDTO("6202354568", "7548003285", "Hii Everyone");
//        return ResponseEntity.ok(biDTO);
//    }

//    @GetMapping("/receieve/message")
//    public ModelAndView createNotificationUser(@RequestBody NotificationUser notificationUser){
//        ModelAndView modelAndView = new ModelAndView();
//        NotificationUser notificationUser1 = notificationUserService.createNotificationUser(notificationUser);
//        modelAndView.addObject("NotificationUser", notificationUserService.getAllNotificationUser());
//        modelAndView.setViewName("NotificationUserDetails");
//        return modelAndView;
//        //return ResponseEntity.status(HttpStatus.CREATED).body(notificationUser1);
//    }
//@GetMapping("/receieve/message")
//public ModelAndView createNotificationUser(@PathVariable String fromNumber,@PathVariable String toNumber,@PathVariable String message){
//     BiDTO notificationUser = notificationUserService.createNotificationUser(fromNumber, toNumber, message);
//    ModelAndView modelAndView = new ModelAndView();
//    modelAndView.addObject("NotificationUser",notificationUser);
//    modelAndView.setViewName("NotificationUserDetails");
//    return modelAndView;
//}

//    @GetMapping(value = "/all/message")
//    public ModelAndView findAll() {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("NotificationUser", notificationUserService.getAllNotificationUser());
//        modelAndView.setViewName("NotificationUserDetails");
//        return modelAndView;
//    }

//    @GetMapping("/receieve/message")
//    public ModelAndView createNotificationUser(@RequestBody NotificationUser notificationUser){
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("NotificationUsers", notificationUserService.createNotificationUser(notificationUser));
//        modelAndView.setViewName("NotificationUserDetails");
//       return modelAndView;
//
//    }



        @GetMapping("/api/{fromNumber}/{toNumber}/{message}")
        public String myApiEndpoint(@PathVariable String fromNumber,
                                    @PathVariable String toNumber,
                                    @PathVariable String message,
                                    Model model) {
            // Logic to process the data


            BiDTO notificationUser = notificationUserService.createNotificationUser(fromNumber, toNumber, message);


            NotificationUser notificationUser1 = notificationUserService.getNotificationUserByMessage(notificationUser.getId());

            model.addAttribute("Notification", notificationUser1);

            return "NotificationUserDetails";
        }






    @GetMapping("/login")
    public String getLogin(){
        return "login";
    }

    @GetMapping("/")
    public String getIndex() {
        return "redirect:/app/";
    }


    @GetMapping("/reset/password")
    public ModelAndView resetPassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("changePassword", new ChangePasswordDTO());
        //System.out.println(passwords);
        modelAndView.setViewName("reset-password");
        return modelAndView;
    }

    //    @PostMapping("/resetPassword")
//	public ModelAndView updatePassword(@ModelAttribute("passwords") CreatePasswordDTO createPasswordDTO) {
//    	ModelAndView modelAndView = new ModelAndView();
//		CreatePasswordDTO ressetPassword = resetPasswordService.ressetPassword(createPasswordDTO);
//        //modelAndView.setViewName(createPasswordDTO.getPassword());
//        System.out.println(ressetPassword+"satyaprakash swain");
//		modelAndView.setViewName("redirect:/login");
//		return modelAndView;
//    }
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ModelAndView changePassword(@ModelAttribute("changePassword") ChangePasswordDTO changePassword, @AuthenticationPrincipal DomainUser domainUser) {
        ModelAndView modelAndView = new ModelAndView();

        boolean passwordChanged = userService.changePassword(changePassword, domainUser);
        if(passwordChanged) {
            modelAndView.addObject("changePassword", new ChangePasswordDTO());
            modelAndView.addObject("message", "Password changed");
        } else {
            changePassword.setConfirmPassword("");
            modelAndView.addObject("changePassword", changePassword);
            modelAndView.addObject("message", "Failed to change the password");
        }

        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }
}



