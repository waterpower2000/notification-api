package com.sysbean.campusive.app.repository;


import com.sysbean.campusive.app.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupsRepository extends JpaRepository<Group, String> {
}
