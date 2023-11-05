package com.sysbean.campusive.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterimStudentDataDTO {
    private String classId;
    private String studentId;
    private long rollNumber;
    private String admissionNo;
    private String studentName;
    private String studentPhone;
    private String studentEmail;
    private LocalDate studentDob;
    private String studentBg;
    private String parentName;
}
