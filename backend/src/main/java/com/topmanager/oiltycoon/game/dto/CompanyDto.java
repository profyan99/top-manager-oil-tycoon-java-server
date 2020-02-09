package com.topmanager.oiltycoon.game.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.topmanager.oiltycoon.game.model.game.company.Company;
import com.topmanager.oiltycoon.game.model.game.company.CompanyPeriodData;
import com.topmanager.oiltycoon.game.model.game.company.CompanySolutions;
import com.topmanager.oiltycoon.game.model.game.company.CompanyStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CompanyDto {
    private CompanyStatisticsDto statistics;
    private CompanyStore store;
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

    private int futurePower;
    private int fullPower;
    private double usingPower;
    private int amortization;
    private int additionalInvestments;

    private double productionCost;

    public CompanyDto(CompanyPeriodData data) {
        this(
                new CompanyStatisticsDto(data),
                data.getStore(),
                data.getSolutions(),
                data.getBank(),
                data.getLoan(),
                data.getActiveStorage(),
                data.getKapInvests(),
                data.getSumActives(),
                data.getRevenue(),
                data.getSPPT(),
                data.getBankInterest(),
                data.getGrossIncome(),
                data.getProfitTax(),
                data.getTax(),
                data.getNetProfit(),
                data.getFuturePower(),
                data.getFullPower(),
                data.getUsingPower(),
                data.getAmortization(),
                data.getAdditionalInvestments(),
                data.getProductionCost()
        );
    }
}
