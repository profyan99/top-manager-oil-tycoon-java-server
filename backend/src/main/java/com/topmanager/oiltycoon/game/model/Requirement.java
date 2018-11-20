package com.topmanager.oiltycoon.game.model;

import com.topmanager.oiltycoon.social.model.Achievement;
import com.topmanager.oiltycoon.social.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Requirement {
    private int minHoursInGameAmount;
    private List<Achievement> requireAchievements;
    private List<UserRole> requireRoles;
}
