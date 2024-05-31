package com.exercise.mycoolservice.config;

import com.exercise.mycoolservice.jwt.JwtRequestFilter;
import com.exercise.mycoolservice.opa.OpaAuthorizationManager;
import com.exercise.mycoolservice.opa.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@Slf4j
@EnableWebSecurity
public class SecurityConfig {

    /*
        LOAD USER-DETAILS (NAME, PWD, ROLE) IN MEMORY TO AUTHENTICATE USER.
     */
    @Bean
    public ReactiveUserDetailsService userDetailsService() {

        log.debug("Initiating default user-details...");

        UserDetails user1 = User.withUsername("swisscom")
                .password(passwordEncoder().encode("password")).roles(Role.ADMIN.getRole())
                .build();
        UserDetails user2 = User.withUsername("user")
                .password(passwordEncoder().encode("password")).roles(Role.USER.getRole())
                .build();
        return new MapReactiveUserDetailsService(user1, user2);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("Initiating passwordEncoder...");
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         @Autowired JwtRequestFilter jwtRequestFilter, @Autowired OpaAuthorizationManager authorizationManager) {

        log.debug("Initializing SecurityWebFilterChain with custom configurations...");
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/authenticate").permitAll()
                        .pathMatchers("/api/users").access(authorizationManager)
                        .anyExchange().authenticated())
                .addFilterAt(jwtRequestFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }
}