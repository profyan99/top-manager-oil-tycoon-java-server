package com.topmanager.oiltycoon.social.dto.response;

import com.topmanager.oiltycoon.social.model.Achievement;
import com.topmanager.oiltycoon.social.model.Reward;
import com.topmanager.oiltycoon.social.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameStatsDto {
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
