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
    private int initialAccumulatedProfit;
    private double marketingPart;

    private int futurePower;
    private int fullPower;
    private double usingPower;
    private int amortization;
    private int additionalInvestments;

    private double productionCost;
    private int productionCostAll;

    private int sumMarketing;
    private int sumNir;
    private int sumProduction;

    private int rating;
    //need for calculation
    private int maxPredictedSales;
    private int maxPredictedRevenue;

    private double shareEffectMk;
    private double shareEffectRd;
    private double shareEffectPr;

    public CompanyPeriodData(Integer period) {
        this(null, period, new CompanyStore(), new CompanySolutions(),
                0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,
                0,0,0,0,0,0,0);
    }

    public CompanyPeriodData(Integer period, CompanyStore store, CompanySolutions solutions, int bank, int loan,
                             int initialAccumulatedProfit, double productionCost, int futurePower) {
        this(null, period, store, solutions,
                bank,loan,0,0,0,0,0,0,0,
                0,0,0,0,initialAccumulatedProfit,0,futurePower,0,
                0,0,0,productionCost,0,0,0,
                0,0,0,0,0,0,0);
    }
}
