package com.topmanager.oiltycoon.game.dto;

import com.topmanager.oiltycoon.game.model.game.Company;

public class CompanyDto {
    private String name;
    private int rating;
    private int oilFactoriesAmount;
    private int gasStationsAmount;
    private int oilWellsAmount;

    public CompanyDto(String name, int rating, int oilFactoriesAmount, int gasStationsAmount, int oilWellsAmount) {
        this.name = name;
        this.rating = rating;
        this.oilFactoriesAmount = oilFactoriesAmount;
        this.gasStationsAmount = gasStationsAmount;
        this.oilWellsAmount = oilWellsAmount;
    }

    public CompanyDto(Company company) {
        this(
                company.getName(),
                company.getRating(),
                company.getOilFactories().size(),
                company.getGasStations().size(),
                company.getOilWells().size()
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

    public int getOilFactoriesAmount() {
        return oilFactoriesAmount;
    }

    public void setOilFactoriesAmount(int oilFactoriesAmount) {
        this.oilFactoriesAmount = oilFactoriesAmount;
    }

    public int getGasStationsAmount() {
        return gasStationsAmount;
    }

    public void setGasStationsAmount(int gasStationsAmount) {
        this.gasStationsAmount = gasStationsAmount;
    }

    public int getOilWellsAmount() {
        return oilWellsAmount;
    }

    public void setOilWellsAmount(int oilWellsAmount) {
        this.oilWellsAmount = oilWellsAmount;
    }
}
