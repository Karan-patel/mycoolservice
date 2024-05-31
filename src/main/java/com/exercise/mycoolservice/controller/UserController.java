package com.exercise.mycoolservice.controller;

import com.exercise.mycoolservice.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private List<User> users = new ArrayList<>();

    public UserController() {
        users.add(new User("John Wick", "john@swisscom.com"));
        users.add(new User("Keanu Reeves", "Keanu@swisscom.com"));
        users.add(new User("Pilar Alonso", "Pilar@swisscom.com"));
    }

    @GetMapping
    public Mono<ResponseEntity<List<User>>> getUsers() {
        return Mono.just(new ResponseEntity<>(users, HttpStatus.OK));
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createUser(@RequestBody User user) {
        users.add(user);
        return Mono.just(new ResponseEntity<>("User created successfully", HttpStatus.CREATED));
    }
}
