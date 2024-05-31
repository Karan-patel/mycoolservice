package com.exercise.mycoolservice.controller;

import com.exercise.mycoolservice.jwt.JwtUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class AuthenticationController {

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    @PostMapping("/authenticate")
    public Mono<Map<String, String>> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        log.info("Authenticating and generating jwt token");
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()))
                .flatMap(authentication -> userDetailsService.findByUsername(authenticationRequest.getUsername())
                        .flatMap(userDetails -> {
                            List<String> roles = userDetails.getAuthorities().stream()
                                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                                    .toList();
                            final String jwt = jwtUtil.generateToken(userDetails.getUsername(), Collections.singletonMap("roles", roles));
                            Map<String, String> response = new HashMap<>();
                            response.put("token", jwt);
                            return Mono.just(response);
                        }));
    }
}

@Data
class AuthenticationRequest {
    private String username;
    private String password;
}