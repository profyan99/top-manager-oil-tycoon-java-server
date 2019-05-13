package com.topmanager.oiltycoon.social.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GameStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int gamesAmount;
    private int winAmount;
    private int loseAmount;
    private int tournamentAmount;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Reward> rewards;
    private int maxRevenue;
    private int maxRIF;
    private int hoursInGame;
    private int leaveGameAmount;
    //TODO complain is a separate object with description, author, date
    private int complainAmount;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Achievement> achievements;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

}
