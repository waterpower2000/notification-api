package com.sysbean.campusive.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CampusiveNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusiveNotificationApplication.class, args);
	}

}
