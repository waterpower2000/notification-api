package com.sysbean.campusive.app.controller;

import com.sysbean.campusive.app.dto.CreateGroupsDTO;
import com.sysbean.campusive.app.dto.CreateStudentDTO;
import com.sysbean.campusive.app.dto.GroupsDTO;
import com.sysbean.campusive.app.service.GroupService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app")
public class UserController {
    private final GroupService groupService;

    public UserController(GroupService groupService) {
        this.groupService = groupService;
    }


    @GetMapping("/logout")
    public String fetchSignoutSite(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login";
    }

    @GetMapping("/")
    public ModelAndView getIndex(){
        ModelAndView modelAndView = new ModelAndView();


        List<GroupsDTO> groups = groupService.getAllGroupsDetail();

        modelAndView.addObject("groups", new CreateGroupsDTO());
        modelAndView.addObject("allGroups", groups);
        modelAndView.setViewName("groups");

        return modelAndView;
    }
    @GetMapping("/student-all")
    public ModelAndView getStudents(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("students", new CreateStudentDTO());
        modelAndView.setViewName("students");

        return modelAndView;
    }
    @GetMapping("/notification-all")
    public ModelAndView getNotification(){
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("notification-1");

        return modelAndView;
    }
}
