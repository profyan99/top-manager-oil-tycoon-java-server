package com.topmanager.oiltycoon.social.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GameStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int gamesAmount;
    private int winAmount;
    private int loseAmount;
    private int tournamentAmount;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private Set<Reward> rewards;

    private int maxRevenue;
    private int maxRIF;
    private int hoursInGame;
    private int leaveGameAmount;
    //TODO complain is a separate object with description, author, date
    private int complainAmount;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private Set<Achievement> achievements;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public GameStats(int gamesAmount, int winAmount, int loseAmount, int tournamentAmount, int maxRevenue, int maxRIF,
                     int hoursInGame, int leaveGameAmount, int complainAmount) {
        this(
                null,
                gamesAmount,
                winAmount,
                loseAmount,
                tournamentAmount,
                new HashSet<>(),
                maxRevenue,
                maxRIF,
                hoursInGame,
                leaveGameAmount,
                complainAmount,
                new HashSet<>(),
                null
        );
    }
}
