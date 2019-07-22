package com.topmanager.oiltycoon.game.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Factory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private int nir;
    private int investments;
    private int cost;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<OilType, Integer> productionPower;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<OilType, Integer> productCostPrice;

    public static final int MAX_FACTORIES_BY_PLAYER = 3;


}
