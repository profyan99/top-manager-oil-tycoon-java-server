package com.topmanager.oiltycoon.dto;

import com.topmanager.oiltycoon.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    private String userName;

    private String firstName;
    private String lastName;
    private Set<UserRole> roles;
}
