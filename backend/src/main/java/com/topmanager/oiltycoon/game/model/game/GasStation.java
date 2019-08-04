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
public class GasStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ElementCollection
    @CollectionTable(
            name = "gas_station_data",
            joinColumns = @JoinColumn(name = "gas_station_id"),
            foreignKey = @ForeignKey(name = "gas_station_data_gas_station_fk")
    )
    private Map<Integer, GasStationData> periodData;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class GasStationData {
        private int marketing;
        private int image;
        private int cost;
        private double productCost;
    }
}
