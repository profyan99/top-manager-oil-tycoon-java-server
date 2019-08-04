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

    @ElementCollection
    @CollectionTable(
            name = "factory_data",
            joinColumns = @JoinColumn(name = "factory_id"),
            foreignKey = @ForeignKey(name = "factory_data_factory_fk")
    )
    private Map<Integer, FactoryData> periodData;

    public static final int MAX_FACTORIES_BY_PLAYER = 3;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class FactoryData {
        private int nir;
        private int investments;
        private int cost;
        private int productionPower;
        private int productCostPrice;

    }
}
