package com.topmanager.oiltycoon.game.model;

import com.topmanager.oiltycoon.social.model.Achievement;
import com.topmanager.oiltycoon.social.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int minHoursInGameAmount;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Achievement> requireAchievements;

    @ElementCollection
    private Set<UserRole> requireRoles;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
