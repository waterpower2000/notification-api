package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.dto.CreateStudentDTO;
import com.sysbean.campusive.app.dto.StudentDTO;
import com.sysbean.campusive.app.dto.StudentDataTempDTO;
import com.sysbean.campusive.app.model.Students;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {
    List<Students> getAllStudents();
    List<StudentDTO> getAllStudentDetail();

    Students getStudentById(String studentId);

    StudentDTO getStudentByIdDetails(String studentId);

    List<StudentDTO> getAllStudentsByGroup(String groupId);
    
    List<Students> getAllStudentGroup(String groupId);

    Students getStudentByPhoneNumber(String phoneNumber);

    StudentDTO getStudentByPhoneNumberDetails(String phoneNumber);

    StudentDTO createStudent(CreateStudentDTO createStudentDTO);

    StudentDTO updateStudent(String studentId, CreateStudentDTO createStudentDTO);

    void deleteStudent(String studentId);

    StudentDTO convertIntoDTO(Students students);

    byte[] downloadBulkStudentData();

    StudentDataTempDTO uploadBulkStudent(MultipartFile file);

    void confirmBulkUpload(String requestId);
}
