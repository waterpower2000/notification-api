package com.sysbean.campusive.app.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDTO {
    private String studentId;
    private String groupId;
    private String groupName;
    private String studentName;
    private String phoneNumber;
    private String emailId;
    private long rollNumber;
    private String admissionNumber;
    private String dob;
    private String bg;
    private String parent;
}
