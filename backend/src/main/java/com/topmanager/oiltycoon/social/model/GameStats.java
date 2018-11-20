package com.topmanager.oiltycoon.social.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameStats {
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
}
