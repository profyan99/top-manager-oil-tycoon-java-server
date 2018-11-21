package com.topmanager.oiltycoon.social.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class GameStats {
    private int id;
    private int gamesAmount;
    private int winAmount;
    private int tournamentAmount;
    private List<Reward> rewards;
    private int maxRevenue;
    private int maxRIF;
    private int hoursInGame;
    private int leaveGameAmount;
    //TODO complain is a separate object with description, author, date
    private int complainAmount;
    private List<Achievement> achievements;
    private User user;

    public GameStats(int gamesAmount, int winAmount, int tournamentAmount, List<Reward> rewards, int maxRevenue,
                     int maxRIF, int hoursInGame, int leaveGameAmount, int complainAmount, List<Achievement> achievements) {
        this(0, gamesAmount, winAmount, tournamentAmount, rewards, maxRevenue, maxRIF, hoursInGame,
                leaveGameAmount, complainAmount, achievements, new User());
    }
}
