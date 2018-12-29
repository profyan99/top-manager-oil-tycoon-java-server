package com.topmanager.oiltycoon.game.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.topmanager.oiltycoon.social.model.Achievement;
import com.topmanager.oiltycoon.social.model.UserRole;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int minHoursInGameAmount;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    private Set<Achievement> requireAchievements;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRole> requireRoles;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference
    private Room room;
}
