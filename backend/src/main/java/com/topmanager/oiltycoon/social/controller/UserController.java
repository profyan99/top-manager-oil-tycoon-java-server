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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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
        userService.create(signUpRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "auth/{provider}")
    public RedirectView handleSocialAuth(@PathVariable String provider) {
        return new RedirectView("/auth/" + provider);
    }

    @GetMapping(path = "profile")
    public ResponseEntity<?> profile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @GetMapping(path = "profile/{userName}")
    public ResponseEntity<?> getProfileByName(@PathVariable String userName) {
        return ResponseEntity.ok(userService.getUserProfileByUserName(userName));
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
