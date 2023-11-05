package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.dto.CreateGroupsDTO;
import com.sysbean.campusive.app.dto.GroupsDTO;
import com.sysbean.campusive.app.model.Group;

import java.util.List;

public interface GroupService {
    List<Group> getAllGroups();
    List<GroupsDTO> getAllGroupsDetail();

    Group getGroupsById(String groupId);

    GroupsDTO getGroupByIdDetails(String groupId);

    GroupsDTO createGroup(CreateGroupsDTO createGroupsDTO);

    GroupsDTO updateGroup(String groupId, CreateGroupsDTO createGroupsDTO);

    void deleteGroup(String groupId);

    GroupsDTO convertIntoDTO(Group groups);
}
