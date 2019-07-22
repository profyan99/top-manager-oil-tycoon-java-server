package com.topmanager.oiltycoon.game.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Store {
    private int oilAmount;
    private Map<OilType, Integer> productAmount;
    private Map<OilType, Integer> exportAmount;
    //TODO futures / forwards
}
