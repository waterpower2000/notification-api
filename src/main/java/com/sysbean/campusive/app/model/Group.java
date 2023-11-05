package com.sysbean.campusive.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "TB_STUDENT_GROUP")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @GenericGenerator(name = "Application-Generic-Generator", strategy = "com.sysbean.campusive.app.config.ApplicationIdentityGenerator")
    @GeneratedValue(generator = "Application-Generic-Generator")
    @Column(name = "GROUP_ID", nullable = false, unique = true)
    private String groupId;

    @Column(name = "GROUP_NAME", nullable = false)
    private String groupName;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;
    public Group(String groupName){
        this.groupName = groupName;
        this.createdOn = LocalDateTime.now();
    }

}

