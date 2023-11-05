package com.sysbean.campusive.app.controller;


import com.sysbean.campusive.app.model.NotificationUser;
import com.sysbean.campusive.app.service.NotificationUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotificationUserController {
	@Autowired
	private NotificationUserService notificationUserService;
}
