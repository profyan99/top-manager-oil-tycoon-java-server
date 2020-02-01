package com.topmanager.oiltycoon.game.model.game.company;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CompanyPeriodData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer period;

    @Embedded
    @JsonManagedReference
    private CompanyStatistics statistics;

    @Embedded
    @JsonManagedReference
    private CompanyStore store;

    @Embedded
    @JsonManagedReference
    private CompanySolutions solutions;

    private int bank;
    private int loan;
    private int activeStorage;
    private int kapInvests;
    private int sumActives;

    private int revenue;
    private int SPPT;
    private int bankInterest;
    private int grossIncome;
    private int profitTax;
    private int tax;
    private int netProfit;
    private int accumulatedProfit;

    private int futurePower;
    private int fullPower;
    private double usingPower;
    private int amortization;
    private int additionalInvestments;

    private double productionCost;
    private int productionCostAll;
}
