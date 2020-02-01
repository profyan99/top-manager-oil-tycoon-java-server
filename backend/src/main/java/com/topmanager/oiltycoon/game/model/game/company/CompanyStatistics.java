package com.topmanager.oiltycoon.game.model.game.company;

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
public class CompanyStatistics {
    private int rating;
    private int price;
    private int revenue;
    private int netProfit;
    private int accumulatedProfit;
    private double marketingPart;
}
