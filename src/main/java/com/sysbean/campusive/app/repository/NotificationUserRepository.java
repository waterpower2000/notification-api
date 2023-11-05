package com.sysbean.campusive.app.repository;

import com.sysbean.campusive.app.model.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUser,String> {
   // NotificationUser findBymessage(String fromNumber,String toNumber,String message);
    //NotificationUser findByfromNumberAndtoNumberAndmessage(String fromNumber,String toNumber,String message);

//    @Query("SELECT e FROM TB_NOTIFICATION_DETAILS e WHERE e.fromNumber = :param1 AND e.toNumber = :param2 AND e.message = :param3")
//    NotificationUser findByProperties(@Param("param1") String param1, @Param("param2") String param2, @Param("param3") String param3);

}
