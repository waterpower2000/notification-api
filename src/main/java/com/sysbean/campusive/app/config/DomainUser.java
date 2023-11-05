package com.sysbean.campusive.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DomainUser extends User {

    private String firstName;
    private String lastName;
    private String loginId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;

    public DomainUser(String username, String password, boolean enabled, List<String> authorities) {
        super(username, password, enabled, true, true, true,
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }
}
