package com.topmanager.oiltycoon.social.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private int id;
    private String ip;
    private String email;
    private String userName;

    private String firstName;
    private String lastName;
    private String password;
    private Set<UserRole> roles;
    private GameStats gameStats;

    public User(String email, String userName, String firstName, String lastName, String password, Set<UserRole> roles,
                GameStats gameStats) {
        this.email = email;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roles = roles;
        this.id = 0;
        this.gameStats = gameStats;
    }


}
