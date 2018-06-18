package com.topmanager.oiltycoon.controller;

import com.topmanager.oiltycoon.dto.request.SignUpRequestDto;
import com.topmanager.oiltycoon.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        return ResponseEntity.ok(userService.create(signUpRequestDto));
    }

    @GetMapping(path = "profile")
    public ResponseEntity<?> profile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

}
