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
public class OilWell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private int startPeriod;

    @ElementCollection
    @CollectionTable(
            name = "oil_well_data",
            joinColumns = @JoinColumn(name = "oil_well_id"),
            foreignKey = @ForeignKey(name = "oil_well_data_oil_well_fk")
    )
    private Map<Integer, OilWellData> periodData;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class OilWellData {
        private int nir;
        private int power;
        private int cost;
        private double oilCostPrice;
    }
}
