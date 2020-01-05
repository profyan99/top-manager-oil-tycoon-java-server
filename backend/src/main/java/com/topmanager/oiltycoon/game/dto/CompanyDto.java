package com.topmanager.oiltycoon.game.dto;

import com.topmanager.oiltycoon.game.model.game.Company;

public class CompanyDto {
    private String name;
    private int rating;

    public CompanyDto(String name, int rating) {
        this.name = name;
        this.rating = rating;
    }

    public CompanyDto(Company company) {
        this(
                company.getName(),
                company.getRating()
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
