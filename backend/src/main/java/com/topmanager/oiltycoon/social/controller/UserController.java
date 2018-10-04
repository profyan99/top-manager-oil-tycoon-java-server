package com.topmanager.oiltycoon.social.controller;

import com.topmanager.oiltycoon.social.dto.request.ProfileEditRequestDto;
import com.topmanager.oiltycoon.social.dto.request.ResetPasswordRequestDto;
import com.topmanager.oiltycoon.social.dto.request.SignUpRequestDto;
import com.topmanager.oiltycoon.social.security.annotation.IsAdmin;
import com.topmanager.oiltycoon.social.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PutMapping(path = "signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        return ResponseEntity.ok(userService.create(signUpRequestDto));
    }

    @GetMapping(path = "profile")
    public ResponseEntity<?> profile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @PostMapping(path = "profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editProfile(@RequestBody @Valid ProfileEditRequestDto profileEditRequestDto) {
        return ResponseEntity.ok(userService.edit(profileEditRequestDto));
    }

    @GetMapping(path = "verification")
    public ResponseEntity<?> verification(@RequestParam("token") String token) {
        userService.verification(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequestDto resetPasswordRequestDto) {
        return ResponseEntity.ok(userService.resetPassword(resetPasswordRequestDto));
    }

    @DeleteMapping(path = "profile")
    @IsAdmin
    public ResponseEntity<?> deleteAccount(@RequestParam("id") int id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    

}
