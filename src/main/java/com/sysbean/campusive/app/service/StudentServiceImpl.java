package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.dto.CreateStudentDTO;
import com.sysbean.campusive.app.dto.StudentDTO;
import com.sysbean.campusive.app.dto.StudentDataTempDTO;
import com.sysbean.campusive.app.model.Group;
import com.sysbean.campusive.app.model.NotificationDefaulter;
import com.sysbean.campusive.app.model.Students;
import com.sysbean.campusive.app.repository.StudentsRepository;
import com.sysbean.campusive.app.util.CommonUtil;
import com.sysbean.campusive.app.util.StudentDataReadExcel;
import com.sysbean.campusive.app.util.StudentDataWriteExcel;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentsRepository studentsRepository;
    private final GroupService groupService;
    private final StudentDataReadExcel studentDataReadExcel;

    private final NotificationDefaulterService notificationDefaulterService;
    private final StudentDataWriteExcel studentDataWriteExcel;

    public StudentServiceImpl(StudentsRepository studentsRepository, GroupService groupService, StudentDataReadExcel studentDataReadExcel, NotificationDefaulterService notificationDefaulterService, StudentDataWriteExcel studentDataWriteExcel) {
        this.studentsRepository = studentsRepository;
        this.groupService = groupService;
        this.studentDataReadExcel = studentDataReadExcel;
        this.notificationDefaulterService = notificationDefaulterService;
        this.studentDataWriteExcel = studentDataWriteExcel;
    }

    @Override
    public List<Students> getAllStudents() {
        return studentsRepository.findAll();
    }

    @Override
    public List<StudentDTO> getAllStudentDetail() {
        return getAllStudents().stream().map(this::convertIntoDTO).collect(Collectors.toList());
    }

    @Override
    public Students getStudentById(String studentId) {
        Optional<Students> studentsOptional = studentsRepository.findById(studentId);
        if(!studentsOptional.isPresent()){
            throw new RuntimeException("No Student with this id found");
        }
        return studentsOptional.get();
    }

    @Override
    public StudentDTO getStudentByIdDetails(String studentId) {
        return convertIntoDTO(getStudentById(studentId));
    }
    @Override
    public List<StudentDTO> getAllStudentsByGroup(String groupId){
        Group group = groupService.getGroupsById(groupId);
        List<Students> studentsList = studentsRepository.findByGroup(group);

        return studentsList.stream().map(this::convertIntoDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<Students> getAllStudentGroup(String groupId) {
        Group group = groupService.getGroupsById(groupId);
        List<Students> studentsList = studentsRepository.findByGroup(group);
        return studentsList;
    }
    
    @Override
    public Students getStudentByPhoneNumber(String phoneNumber){
        Optional<Students> studentsOptional = studentsRepository.findByPhoneNumber(phoneNumber);
        if(!studentsOptional.isPresent()){
            throw new RuntimeException("No Student with this id found");
        }
        return studentsOptional.get();
    }
    @Override
    public StudentDTO getStudentByPhoneNumberDetails(String phoneNumber) {
        return convertIntoDTO(getStudentByPhoneNumber(phoneNumber));
    }


    @Override
    public StudentDTO createStudent(CreateStudentDTO createStudentDTO) {
        Group group = groupService.getGroupsById(createStudentDTO.getGroupId());

        Students students = new Students(group, UUID.randomUUID().toString(), createStudentDTO.getRollNumber(), createStudentDTO.getAdmissionNumber(),
                createStudentDTO.getStudentName(), createStudentDTO.getPhoneNumber(), createStudentDTO.getEmailId(),createStudentDTO.getDob() ,
                createStudentDTO.getBg(), createStudentDTO.getParent());
        return convertIntoDTO(studentsRepository.save(students));
    }
//CommonUtil.getDob(createStudentDTO.getDob())
    @Override
    public StudentDTO updateStudent(String studentId, CreateStudentDTO createStudentDTO) {

        Students existingStudent = getStudentById(studentId);

        Group group = groupService.getGroupsById(createStudentDTO.getGroupId());
        existingStudent.setStudentName(createStudentDTO.getStudentName());
        existingStudent.setPhoneNumber(createStudentDTO.getPhoneNumber());
        existingStudent.setStudentEmailId(createStudentDTO.getEmailId());
        existingStudent.setAdmissionNo(createStudentDTO.getAdmissionNumber());
        existingStudent.setStudentBloodGroup(createStudentDTO.getBg());
        existingStudent.setStudentDob(createStudentDTO.getDob()!= null?createStudentDTO.getDob(): null);
        existingStudent.setStudentParentName(createStudentDTO.getParent());
        existingStudent.setGroup(group);
        return convertIntoDTO(studentsRepository.save(existingStudent));
    }

    @Override
    public void deleteStudent(String studentId) {
        List<NotificationDefaulter> notificationDefaulter = notificationDefaulterService.getStudentNotificationDetaulter(studentId);
        List<String> notificationDefaulterId = notificationDefaulter.stream().map(nd -> nd.getNotificationDefaulterId()).collect(Collectors.toList());

        if(notificationDefaulter != null){
            notificationDefaulterService.deleteNotificationDefaulterStudent(notificationDefaulterId);
        }
        Students existingStudent = getStudentById(studentId);
        studentsRepository.delete(existingStudent);
    }

    @Override
    public StudentDTO convertIntoDTO(Students students) {
        if(students== null) {
          return null;
        }
        return new StudentDTO(students.getStudentId(), students.getGroup().getGroupId(), students.getGroup().getGroupName(), students.getStudentName(), students.getPhoneNumber(),
                students.getStudentEmailId(), students.getStudentRollNo(), students.getAdmissionNo(), students.getStudentDob(), students.getStudentBloodGroup(), students.getStudentParentName());
    }
//CommonUtil.getDob(students.getStudentDob())
    @Override
    public byte[] downloadBulkStudentData() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            studentDataReadExcel.writeStudentData(workbook);
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to download".getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public StudentDataTempDTO uploadBulkStudent(MultipartFile file) {
        File convertedFile = CommonUtil.convertMultiPartToFile(file);
        String requestId = UUID.randomUUID().toString();
        List<String> warning = new ArrayList<>();
        List<String> error = new ArrayList<>();
        List<Map<String, String>> newEntry = new ArrayList<>();
        List<Map<String, String>> updatedEntry = new ArrayList<>();

        studentDataWriteExcel.writeTempData(convertedFile, requestId, warning, error, newEntry, updatedEntry);

        StudentDataTempDTO studentDataTemp = new StudentDataTempDTO();
        studentDataTemp.setRequestId(requestId);
        studentDataTemp.setWarning(warning);
        studentDataTemp.setError(error);
        studentDataTemp.setNewEntry(newEntry);
        studentDataTemp.setUpdatedEntry(updatedEntry);

        return studentDataTemp;
    }

    @Override
    public void confirmBulkUpload(String requestId) {
        studentDataWriteExcel.storeNewStudentData(requestId);
        studentDataWriteExcel.storeChangedStudentData(requestId);
    }
}
