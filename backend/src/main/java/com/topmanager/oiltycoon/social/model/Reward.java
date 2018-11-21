package com.topmanager.oiltycoon.social.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

public class Reward {
    private int id;
    private LocalDate date;
    private String name;
    private String description;
    @JsonIgnore
    private GameStats gameStats;
}
