package com.topmanager.oiltycoon.social.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate date;
    private String name;
    private String description;
    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_stats_id")
    @JsonBackReference
    private GameStats gameStats;
}
