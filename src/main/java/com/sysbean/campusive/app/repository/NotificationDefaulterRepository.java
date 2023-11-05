package com.sysbean.campusive.app.repository;

import com.sysbean.campusive.app.model.Notification;
import com.sysbean.campusive.app.model.NotificationDefaulter;
import com.sysbean.campusive.app.model.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationDefaulterRepository extends JpaRepository<NotificationDefaulter, String> {
    List<NotificationDefaulter> findByNotification(Notification notification);

    List<NotificationDefaulter> findByStudent(Students student);
}
