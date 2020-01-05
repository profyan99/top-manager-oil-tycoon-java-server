package com.topmanager.oiltycoon.social.dto;

import com.topmanager.oiltycoon.social.dto.response.GameStatsDto;
import com.topmanager.oiltycoon.social.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    private String userName;
    private String ip;
    private String country;
    private LocalDate registerDate;
    private LocalDate lastLogIn;

    private int reputation;
    private int profileWatchAmount;
    private String avatar;

    private Set<UserRole> roles;
    private String email;
    private GameStatsDto gameStats;
}
