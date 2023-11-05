package com.sysbean.campusive.app.controller;

import com.sysbean.campusive.app.dto.*;
import com.sysbean.campusive.app.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/app/groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }
    @GetMapping("/all")
    public ModelAndView getAllGroups() {
        ModelAndView modelAndView = new ModelAndView();

        List<GroupsDTO> groups = groupService.getAllGroupsDetail();
        modelAndView.addObject("allGroups", groups);
        modelAndView.addObject("groups", new CreateGroupsDTO());
        modelAndView.setViewName("groups");
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView createGroup() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("groups", new CreateGroupsDTO());
        modelAndView.setViewName("add_groups");

        return modelAndView;
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupsDTO> getGroupDetails(@PathVariable String groupId) {
       GroupsDTO group = groupService.getGroupByIdDetails(groupId);

        return ResponseEntity.ok(group);
    }

//    @PostMapping("/")
//    public ResponseEntity<GroupsDTO> createGroup(@RequestBody CreateGroupsDTO createGroupsDTO){
//        GroupsDTO groupsDTO = groupService.createGroup(createGroupsDTO);
//
//        return ResponseEntity.ok(groupsDTO);
//    }

    @PostMapping("/")
    public ModelAndView createGroup(@ModelAttribute("groups") CreateGroupsDTO createGroupsDTO, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        GroupsDTO groupsDTO = groupService.createGroup(createGroupsDTO);
        session.setAttribute("message", new MessageDTO("Group Created Successfully", "Success"));
        List<GroupsDTO> groups = groupService.getAllGroupsDetail();
        modelAndView.addObject("allGroups", groups);


        modelAndView.addObject("message", "Group Successfully Created");

            modelAndView.setViewName("redirect:/app/groups/all");
            return modelAndView;

    }


    @GetMapping("/edit/{id}")
    public ModelAndView editStudent(@PathVariable String id){

        GroupsDTO groups = groupService.getGroupByIdDetails(id);
        System.out.println(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("groups",groups);
        //modelAndView.addObject("students", new CreateStudentDTO());
        modelAndView.setViewName("edit-group");

        return modelAndView;
    }

    @PostMapping("/update/{id}")
    public ModelAndView updateGroup(@ModelAttribute("group") CreateGroupsDTO createGroupsDTO, @PathVariable String id, HttpSession session){

        System.out.println(id);
        GroupsDTO groupsDTO = groupService.updateGroup(id, createGroupsDTO);
        ModelAndView modelAndView = new ModelAndView();
        session.setAttribute("message", new MessageDTO("Group Updated Successfully", "Success"));
        modelAndView.addObject("message", "Group Successfully Updated");
        modelAndView.setViewName("redirect:/app/groups/all");

        return modelAndView;
    }


//    @DeleteMapping("delete/{groupId}")
//    public ResponseEntity<String> deleteGroup(@PathVariable String groupId){
//
//        groupService.deleteGroup(groupId);
//
//        return ResponseEntity.ok("Group Successfully deleted");
//    }
@GetMapping("/delete/{groupId}")
public ModelAndView deleteGroup( @PathVariable String groupId, HttpSession session){

    System.out.println("satya"+groupId);
    groupService.deleteGroup(groupId);
    ModelAndView modelAndView = new ModelAndView();
    session.setAttribute("message", new MessageDTO("Group Deleted Successfully", "Success"));
    modelAndView.addObject("message", "Group Successfully deleted");
    modelAndView.setViewName("redirect:/app/groups/all");

    return modelAndView;
}

}
