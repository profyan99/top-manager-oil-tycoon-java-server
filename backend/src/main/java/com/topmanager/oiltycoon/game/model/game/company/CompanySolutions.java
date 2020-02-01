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
public class CompanySolutions {
    private int price;
    private int production;
    private int marketing;
    private int investments;
    private int nir;
}
