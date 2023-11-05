package com.sysbean.campusive.app.controller;

import com.sysbean.campusive.app.dto.*;
import com.sysbean.campusive.app.service.GroupService;
import com.sysbean.campusive.app.service.StudentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/app/students")
public class StudentController {

    private final StudentService studentService;
    private final GroupService groupService;

    public StudentController(StudentService studentService, GroupService groupService) {
        this.studentService = studentService;
        this.groupService = groupService;
    }

    @GetMapping("/all")
    public ModelAndView getAllStudents(){
        ModelAndView modelAndView = new ModelAndView();
        List<StudentDTO> students = studentService.getAllStudentDetail();
        List<GroupsDTO> groups = groupService.getAllGroupsDetail();
        modelAndView.addObject("groups",groups);
        modelAndView.addObject("students", new CreateStudentDTO());
        modelAndView.addObject("allStudents", students);
        modelAndView.setViewName("students");
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView createStudent() {
        ModelAndView modelAndView = new ModelAndView();
        List<GroupsDTO> groups = groupService.getAllGroupsDetail();
        modelAndView.addObject("groups",groups);
        modelAndView.addObject("students", new CreateStudentDTO());
        modelAndView.setViewName("students");

        return modelAndView;
    }

    @PostMapping("/")
    public ModelAndView createGroup(@ModelAttribute("students") CreateStudentDTO createStudentDTO, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        StudentDTO student = studentService.createStudent(createStudentDTO);
        session.setAttribute("message", new MessageDTO("Student Created Successfully", "Success"));
        modelAndView.addObject("message", "Student Successfully Created");
        modelAndView.setViewName("redirect:/app/students/all");
        return modelAndView;

    }

    @GetMapping("/edit/{id}")
    public ModelAndView editStudent(@PathVariable String id){
        ModelAndView modelAndView = new ModelAndView();
        StudentDTO studentDTO = studentService.getStudentByIdDetails(id);
        List<GroupsDTO> groups = groupService.getAllGroupsDetail();
        modelAndView.addObject("groups",groups);
        System.out.println(id);
        System.out.println(studentDTO.getStudentName());
        System.out.println(studentDTO.getPhoneNumber());

        modelAndView.addObject("student",studentDTO);
        modelAndView.addObject("groups",groups);
        //modelAndView.addObject("students", new CreateStudentDTO());
        modelAndView.setViewName("edit_student");

        return modelAndView;
    }

    @PostMapping("/update/{id}")
    public ModelAndView updateStudent(@ModelAttribute("student") CreateStudentDTO createStudentDTO, @PathVariable String id, HttpSession session){

        System.out.println(createStudentDTO.getStudentName());
        System.out.println(createStudentDTO.getPhoneNumber());
        System.out.println(id);

        StudentDTO studentDTO = studentService.updateStudent(id, createStudentDTO);
        ModelAndView modelAndView = new ModelAndView();
        session.setAttribute("message", new MessageDTO("Student Updated Successfully", "Success"));
        modelAndView.addObject("message", "Student Successfully Updated");
        modelAndView.setViewName("redirect:/app/students/all");

        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteStudent( @PathVariable String id, HttpSession session){

        System.out.println(id);
         studentService.deleteStudent(id);
        ModelAndView modelAndView = new ModelAndView();
        session.setAttribute("message", new MessageDTO("Student Deleted Successfully", "Success"));
        modelAndView.addObject("message", "Student Successfully deleted");
        modelAndView.setViewName("redirect:/app/students/all");

        return modelAndView;
    }

    @GetMapping("/bulk/")
    public ResponseEntity<ByteArrayResource> downloadBulkStudentData() {
        byte[] data = studentService.downloadBulkStudentData();
        ByteArrayResource resource = new ByteArrayResource(data);
        String dateTimeFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-disposition", "attachment; filename=\"CAMPUSIVE_LIONS_STUDENT_"+dateTimeFormat+"\".xlsx")
                .body(resource);
    }

    @PostMapping("/bulk/")
    public ModelAndView uploadBulk(@RequestParam(name = "file", required = true) MultipartFile file) {
        String requestId = UUID.randomUUID().toString();
        StudentDataTempDTO studentDataTemp = studentService.uploadBulkStudent(file);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("record", studentDataTemp);
        modelAndView.setViewName("upload");
        return modelAndView;
    }

    @GetMapping("/bulk/confirm")
    public ModelAndView confirmUpload(@RequestParam(name = "requestId") String requestId) {
        studentService.confirmBulkUpload(requestId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/app/students/all");
        return modelAndView;
    }

}
