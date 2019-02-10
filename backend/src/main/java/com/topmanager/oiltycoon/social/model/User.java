package com.topmanager.oiltycoon.social.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String ip;
    private String country;
    private LocalDate registerDate;
    private LocalDate lastLogIn;
    private String email;
    private String userName;

    private String firstName;
    private String lastName;
    private String password;

    private int reputation;
    private String description;
    private int profileWatchAmount;
    private boolean isOnline;
    private String avatar;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRole> roles;

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private GameStats gameStats;


}
