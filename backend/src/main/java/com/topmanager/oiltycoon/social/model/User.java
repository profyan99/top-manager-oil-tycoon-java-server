package com.topmanager.oiltycoon.social.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String ip;
    private String email;
    private String userName;

    private String firstName;
    private String lastName;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRole> roles;

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = false)
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
