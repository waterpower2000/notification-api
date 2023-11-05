package com.sysbean.campusive.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity(name = "TB_NOTIFICATION_DEFAULTERS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDefaulter {
    @Id
    @GenericGenerator(name = "Application-Generic-Generator", strategy = "com.sysbean.campusive.app.config.ApplicationIdentityGenerator")
    @GeneratedValue(generator = "Application-Generic-Generator")
    @Column(name = "NOTIFICATION_DEFAULTER_ID", nullable = false, unique = true)
    private String notificationDefaulterId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STUDENTS", nullable = false, foreignKey = @ForeignKey(name = "FK_STUDENTS_NOTIFICATION_DEFAULTER"))
    private Students student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NOTIFICATIONS", nullable = false, foreignKey = @ForeignKey(name = "FK_NOTIFICATIONS_NOTIFICATION_DEFAULTER"))
    private Notification notification;
    @Column(name = "ERROR_MESSAGE", nullable = false)
    private String errorMessage;


    public NotificationDefaulter(Students student, Notification notification, String errorMessage){
        this.student = student;
        this.notification = notification;
        this.errorMessage = errorMessage;
    }
}
