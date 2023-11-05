package com.sysbean.campusive.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ApplicationAuthProvider applicationAuthProvider;
    private final ApplicationSessionContextFilter applicationSessionContextFilter;

    public SecurityConfig(ApplicationAuthProvider applicationAuthProvider, ApplicationSessionContextFilter applicationSessionContextFilter) {
        this.applicationAuthProvider = applicationAuthProvider;
        this.applicationSessionContextFilter = applicationSessionContextFilter;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/css/*", "/js/**", "/images/**", "/vendor/**", "/assets/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(applicationAuthProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .formLogin(form ->
                        form.loginPage("/login")
                                .usernameParameter("app_username")
                                .passwordParameter("app_password")
                                .failureUrl("/login?code=1")
                                .defaultSuccessUrl("/app/")
                                .loginProcessingUrl("/performLogin.html")
                .permitAll()
                )
                .logout((logout) ->

                logout.clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .logoutUrl("/app/logout")
                        .logoutSuccessUrl("/login")
                )
                .csrf().csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                .and()
                .sessionManagement().sessionFixation().changeSessionId()
                .maximumSessions(1)
                .expiredUrl("/login?code=2")
                .and()
                .and()
                .securityContext((securityContext) -> securityContext
                        .requireExplicitSave(true)
                )
                .addFilterAfter(applicationSessionContextFilter, SecurityContextHolderFilter.class)
                .authorizeRequests(req -> {
                    req
                            .antMatchers("/app/notifications/download/**").permitAll()
                            //.antMatchers("/public/receive/notification/**").permitAll()
                            .mvcMatchers("/app/**").authenticated()
                            .anyRequest().permitAll();

                });
        return http.build();
    }
}





