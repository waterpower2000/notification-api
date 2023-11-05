package com.sysbean.campusive.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "TB_STUDENTS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Students {
    @Id
    @GenericGenerator(name = "Application-Generic-Generator", strategy = "com.sysbean.campusive.app.config.ApplicationIdentityGenerator")
    @GeneratedValue(generator = "Application-Generic-Generator")
    @Column(name = "STUDENT_ID", nullable = false, unique = true)
    private String studentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CLASS", nullable = false, foreignKey = @ForeignKey(name = "FK_CLASS_STUDENTS"))
    private Group group;

    @Column(name = "STUDENT_ROLL")
    private long studentRollNo;

    @Column(name = "ADMISSION_NO")
    private String admissionNo;

    @Column(name = "STUDENT_NAME", nullable = false)
    private String studentName;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Column(name = "STUDENT_EMAIL")
    private String studentEmailId;

    @Column(name = "STUDENT_DOB")
   // private LocalDate studentDob;
    private String studentDob;


    @Column(name = "STUDENT_BG")
    private String studentBloodGroup;

    @Column(name = "STUDENT_PARENT_NAME")
    private String studentParentName;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;

    public Students(Group group, String studentId, long studentRollNo, String admissionNo, String studentName, String phoneNumber,
                    String studentEmailId, String studentDob, String studentBloodGroup, String studentParentName){
        this.group = group;
        this.studentId = studentId;
        this.studentRollNo = studentRollNo;
        this.admissionNo = admissionNo;
        this.studentName = studentName;
        this.phoneNumber = phoneNumber;
        this.studentEmailId = studentEmailId;
        this.studentDob = studentDob;
        this.studentBloodGroup = studentBloodGroup;
        this.studentParentName = studentParentName;
        this.createdOn = LocalDateTime.now();
    }

}

