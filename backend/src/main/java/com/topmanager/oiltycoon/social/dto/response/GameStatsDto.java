package com.topmanager.oiltycoon.social.dto.response;

import com.topmanager.oiltycoon.social.model.Achievement;
import com.topmanager.oiltycoon.social.model.Reward;
import com.topmanager.oiltycoon.social.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameStatsDto {
    private int id;
    private int gamesAmount;
    private int winAmount;
    private int loseAmount;
    private int tournamentAmount;
    private Set<Reward> rewards;
    private int maxRevenue;
    private int maxRIF;
    private int hoursInGame;
    private int leaveGameAmount;
    //TODO complain is a separate object with description, author, date
    private int complainAmount;
    private Set<Achievement> achievements;
}
