package com.sysbean.campusive.app.repository;


import com.sysbean.campusive.app.model.Group;
import com.sysbean.campusive.app.model.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentsRepository extends JpaRepository<Students, String> {

    List<Students> findByGroup(Group group);

    Optional<Students> findByPhoneNumber(String phoneNumber);
}
