package com.topmanager.oiltycoon.dto;

import com.topmanager.oiltycoon.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String userName;

    private String firstName;
    private String lastName;
    private UserRole role;
}
