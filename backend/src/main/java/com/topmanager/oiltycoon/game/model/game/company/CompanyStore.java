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
public class CompanyStore {
    private int receivedOrders;
    private int machineTools;
    private int storage;
    private int storageCost;
    private int sales;
    private int backlogSales;
}
