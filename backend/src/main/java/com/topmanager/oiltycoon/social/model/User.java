package com.topmanager.oiltycoon.social.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.topmanager.oiltycoon.game.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String ip;
    private String country;
    private LocalDate registerDate;
    private LocalDate lastLogIn;
    private String email;
    private String userName;

    private String password;

    private int reputation;
    private int profileWatchAmount;
    private String avatar;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRole> roles;

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private GameStats gameStats;

    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    @JsonIgnore
    private Player player;

    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JsonIgnore
    private VerificationToken verificationToken;

    public User(String country, LocalDate registerDate, LocalDate lastLogIn, String email, String userName,
                String password, String avatar, Set<UserRole> roles) {
        this(
                null,
                "",
                country,
                registerDate,
                lastLogIn,
                email,
                userName,
                password,
                0,
                0,
                avatar,
                roles,
                new GameStats(
                        0, 0, 0, 0, 0, 0, 0, 0, 0
                ),
                null,
                null

        );
        gameStats.setUser(this);
    }


}
