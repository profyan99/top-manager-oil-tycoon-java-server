package com.topmanager.oiltycoon.game.model.game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Embeddable
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JsonBackReference
    private Player player;

    private String name;

    @MapKey(name = "id")
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "company",
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.DETACH,
                    CascadeType.REFRESH
            },
            orphanRemoval = true
    )
    private Map<Integer, Factory> oilFactories;

    @MapKey(name = "id")
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "company",
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.DETACH,
                    CascadeType.REFRESH
            },
            orphanRemoval = true
    )
    private Map<Integer, GasStation> gasStations;

    @MapKey(name = "id")
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "company",
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.DETACH,
                    CascadeType.REFRESH
            },
            orphanRemoval = true
    )
    private Map<Integer, OilWell> oilWells;

    @Embedded
    @JsonManagedReference
    private Store store;

    private int bank;
    private int rating;
    private int oilExploration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return bank == company.bank &&
                rating == company.rating &&
                Objects.equals(id, company.id) &&
                Objects.equals(name, company.name) &&
                Objects.equals(oilFactories, company.oilFactories) &&
                Objects.equals(gasStations, company.gasStations) &&
                Objects.equals(oilWells, company.oilWells) &&
                Objects.equals(store, company.store);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void addFactory(Factory factory) {
        if (oilFactories.containsValue(factory)) {
            throw new RestException(ErrorCode.FACTORY_ALREADY_EXISTS);
        }
        oilFactories.put(factory.getId(), factory);
        factory.setCompany(this);
    }

    public void addGasStation(GasStation gasStation) {
        if (gasStations.containsValue(gasStation)) {
            throw new RestException(ErrorCode.GAS_STATION_ALREADY_EXISTS);
        }
        gasStations.put(gasStation.getId(), gasStation);
        gasStation.setCompany(this);
    }

    public void addOilWell(OilWell oilWell) {
        if (oilWells.containsValue(oilWell)) {
            throw new RestException(ErrorCode.OIL_WELL_ALREADY_EXISTS);
        }
        oilWells.put(oilWell.getId(), oilWell);
        oilWell.setCompany(this);
    }

    public void removeFactory(Factory factory) {
        oilFactories.remove(factory.getId());
    }

    public void removeGasStation(GasStation station) {
        gasStations.remove(station.getId());
    }

    public void removeOilWell(OilWell oilWell) {
        oilWells.remove(oilWell.getId());
    }
}
