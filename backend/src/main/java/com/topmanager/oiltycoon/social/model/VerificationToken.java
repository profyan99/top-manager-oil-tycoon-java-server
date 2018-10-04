package com.topmanager.oiltycoon.social.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    private int id;

    private String token;

    private User user;

    private LocalDateTime confirmDate;
}
