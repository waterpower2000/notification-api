package com.sysbean.campusive.app.service;

import com.sysbean.campusive.app.dto.CreateGroupsDTO;
import com.sysbean.campusive.app.dto.GroupsDTO;
import com.sysbean.campusive.app.model.Group;
import com.sysbean.campusive.app.repository.GroupsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupsRepository groupsRepository;

    public GroupServiceImpl(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
    }


    @Override
    public List<Group> getAllGroups() {
        return groupsRepository.findAll();
    }

    @Override
    public List<GroupsDTO> getAllGroupsDetail() {
        return getAllGroups().stream().map(this::convertIntoDTO).collect(Collectors.toList());
    }

    @Override
    public Group getGroupsById(String groupId) {

        Optional<Group> groupsOptional = groupsRepository.findById(groupId);
        if(!groupsOptional.isPresent()){
            throw new RuntimeException("No Group found from this id");
        }
        return groupsOptional.get();
    }

    @Override
    public GroupsDTO getGroupByIdDetails(String groupId) {
        return convertIntoDTO(getGroupsById(groupId));
    }

    @Override
    public GroupsDTO createGroup(CreateGroupsDTO createGroupsDTO) {

        Group groups = new Group(createGroupsDTO.getGroupName());
        return convertIntoDTO(groupsRepository.save(groups));
    }

    @Override
    public GroupsDTO updateGroup(String groupId, CreateGroupsDTO createGroupsDTO) {
        Group existingGroup = getGroupsById(groupId);
        existingGroup.setGroupName(createGroupsDTO.getGroupName());
        return convertIntoDTO(groupsRepository.save(existingGroup));
    }

    @Override
    public void deleteGroup(String groupId) {
        Group existingGroup = getGroupsById(groupId);
        groupsRepository.delete(existingGroup);
    }

    @Override
    public GroupsDTO convertIntoDTO(Group groups) {
        if(groups == null){
            return null;
        }
        return new GroupsDTO(groups.getGroupId(), groups.getGroupName(), groups.getCreatedOn().toLocalDate().toString());
    }
}
