package com.topmanager.oiltycoon.game.dto.request;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RoomConnectDto {

    private String password;

    @NotNull
    @NotBlank
    @Length(min = 6)
    private String companyName;

    public RoomConnectDto(String password, String companyName) {
        this.password = password;
        this.companyName = companyName;
    }

    public RoomConnectDto() {

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }
}
