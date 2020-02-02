package com.topmanager.oiltycoon.game.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class GamePeriodData {
    private int summaryMarketing;
    private int summaryNir;
    private int summaryProduction;
    private double totalMarketing; // summM
    private double totalPrice; // summP
    private int totalBuyers;
    private int totalSales;

    private int summaryPeriodPower;
    private int summaryPeriodProduction;
    private double summaryPeriodMarketing;
    private int summaryPeriodSales;
    private int summaryPeriodStorage;
    private int summaryPeriodRevenue;
    private int summaryPeriodKapInvests;
    private double averagePeriodPrice;
    private double averagePeriodProductionCost;
    private double averagePeriodUsingPower;

    public void addSummaryMarketing(int value) {
        this.summaryMarketing += value;
    }

    public void addSummaryNir(int value) {
        this.summaryNir += value;
    }

    public void addSummaryProduction(int value) {
        this.summaryProduction += value;
    }

    public void addTotalMarketing(double value) {
        this.totalMarketing += value;
    }

    public void addTotalPrice(double value) {
        this.totalPrice += value;
    }

    public void addTotalBuyers(int value) {
        this.totalBuyers += value;
    }

    public void addTotalSales(int value) {
        this.totalSales += value;
    }

    public void addSummaryPeriodMarketing(double value) {
        this.summaryPeriodMarketing += value;
    }

    public void addAveragePeriodPrice(double value) {
        this.averagePeriodPrice += value;
    }

    public void addSummaryPeriodPower(int value) {
        this.summaryPeriodPower += value;
    }

    public void addSummaryPeriodProduction(int value) {
        this.summaryPeriodProduction += value;
    }

    public void addSummaryPeriodSales(int value) {
        this.summaryPeriodSales += value;
    }

    public void addSummaryPeriodStorage(int value) {
        this.summaryPeriodStorage += value;
    }

    public void addSummaryPeriodRevenue(int value) {
        this.summaryPeriodRevenue += value;
    }

    public void addSummaryPeriodKapInvests(int value) {
        this.summaryPeriodKapInvests += value;
    }

    public void addAveragePeriodProductionCost(double value) {
        this.averagePeriodProductionCost += value;
    }

    public void addAveragePeriodUsingPower(double value) {
        this.averagePeriodUsingPower += value;
    }


}
