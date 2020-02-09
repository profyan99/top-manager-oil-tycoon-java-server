package com.topmanager.oiltycoon.game.dto;

import com.topmanager.oiltycoon.game.model.game.company.CompanyPeriodData;
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
public class CompanyStatisticsDto {
    private int rating;
    private int price;
    private int revenue;
    private int netProfit;
    private int accumulatedProfit;
    private double marketingPart;

    public CompanyStatisticsDto(CompanyPeriodData data) {
        this(data.getRating(), data.getSolutions().getPrice(), data.getRevenue(), data.getNetProfit(),
                data.getAccumulatedProfit(), data.getMarketingPart()
        );
    }
}
