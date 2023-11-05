package com.sysbean.campusive.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;


@Entity(name = "TB_NOTIFICATION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GenericGenerator(name = "Application-Generic-Generator", strategy = "com.sysbean.campusive.app.config.ApplicationIdentityGenerator")
    @GeneratedValue(generator = "Application-Generic-Generator")
    @Column(name = "NOTIFICATION_ID", nullable = false, unique = true)
    private String notificationId;
    @Column(name = "NOTIFICATION_TITLE", nullable = false)
    private String notificationTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "NOTIFICATION_TYPE", nullable = false)
    private NotificationType notificationType;
//    @ManyToOne(fetch = FetchType.LAZY, optional = true)
//    @JoinColumn(name = "CLASS", foreignKey = @ForeignKey(name = "FK_CLASS_NOTIFICATION"))
//    private Group group;

    @Lob
    @Column(name = "CLASSES")
    private String groups;

    @Lob
    @Column(name = "STUDENTS")
    private String students;



    @Lob
    @Column(name = "ATTACHMENT_LINK")
    private String attachmentLink;
    @Lob
    @Column(name = "NOTIFICATION_BODY")
    private String notificationBody;

    @Lob
    @Column(name = "CreatedBy")
    private String createdBy;



    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_ON", nullable = false)
    private Date createdOn;

    //private LocalDateTime createdOn;

    @PrePersist
    public void onCreate() {
        createdOn= new Date();
    }

    public Notification(String notificationTitle, NotificationType notificationType, String groups, String students, String attachmentLink, String notificationBody,String createdBy){

        this.notificationTitle = notificationTitle;
        this.notificationType = notificationType;
        this.groups = groups;
        this.students = students;
        this.attachmentLink = attachmentLink;
        this.notificationBody = notificationBody;
//        this.createdOn = Date.now();
        this.createdOn = createdOn;
        this.createdBy=createdBy;

    }
}

