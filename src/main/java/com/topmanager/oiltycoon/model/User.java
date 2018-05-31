package com.topmanager.oiltycoon.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private String id;
    private String email;
    private String nickName;

    private String firstName;
    private String lastName;
    private String password;
    private UserRole role;

}
