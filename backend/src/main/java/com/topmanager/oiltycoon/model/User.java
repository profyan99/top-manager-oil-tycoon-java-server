package com.topmanager.oiltycoon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String email;
    private String userName;

    private String firstName;
    private String lastName;
    private String password;
    private UserRole role;

}
