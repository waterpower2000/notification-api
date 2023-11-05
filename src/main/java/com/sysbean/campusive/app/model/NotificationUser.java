package com.sysbean.campusive.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;


    @Entity(name = "TB_NOTIFICATION_DETAILS")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class NotificationUser {
        @Id
        @GenericGenerator(name = "Application-Generic-Generator", strategy = "com.sysbean.campusive.app.config.ApplicationIdentityGenerator")
        @GeneratedValue(generator = "Application-Generic-Generator")
        @Column(name = "ID", nullable = false, unique = true)
        private String Id;

        @Column(name = "FORM_NUMBER", nullable = false)
        private String fromNumber;

        @Column(name = "TO_NUMBER", nullable = false)
        private String toNumber;

        @Column(name = "MESSAGE", nullable = false)
        private String message;

        @CreationTimestamp
        @Column(name = "CREATEED_ON", nullable = false)
        private LocalDate createdOn;


    }


