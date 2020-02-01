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
    private double totalMarketing;
    private double totalPrice;
    private int totalBuyers;
    private int totalSales;
}
