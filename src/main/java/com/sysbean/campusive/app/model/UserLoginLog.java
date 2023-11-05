 package com.sysbean.campusive.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "TB_USER_LOGIN_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginLog {

    @Id
    @GenericGenerator(name = "Application-Generic-Generator", strategy = "com.sysbean.campusive.app.config.ApplicationIdentityGenerator")
    @GeneratedValue(generator = "Application-Generic-Generator")
    @Column(name = "LOGIN_ID", nullable = false, unique = true)
    private String loginId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ENTITY", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_LOGIN_LOG_USER"))
    private UserEntity userEntity;

    @Column(name = "LOGIN_TIME", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "LOGIN_TERMINAL", nullable = false)
    private String loginTerminal;

    @Lob
    @Column(name = "LOGIN_USER_AGENT")
    private String userAgent;

    @Column(name = "LOGOUT_TIME")
    private LocalDateTime logoutTime;
}
