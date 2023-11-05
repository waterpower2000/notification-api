package com.sysbean.campusive.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudentDTO {
    private String groupId;
    private String studentName;
    private String phoneNumber;
    private String emailId;
    private long rollNumber;
    private String admissionNumber;
    private String dob;
    private String bg;
    private String parent;
}
