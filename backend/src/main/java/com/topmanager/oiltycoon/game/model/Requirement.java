package com.topmanager.oiltycoon.game.model;

import com.topmanager.oiltycoon.social.model.Achievement;
import com.topmanager.oiltycoon.social.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Requirement {

    private int minHoursInGameAmount;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private Set<Achievement> requireAchievements;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<UserRole> requireRoles;
}
