package com.topmanager.oiltycoon.game.model.game.company;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.topmanager.oiltycoon.game.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
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
    @JsonBackReference
    private Player player;

    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "company_company_period_data_mapping",
            joinColumns = {@JoinColumn(name = "company_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "company_period_data_id", referencedColumnName = "id")})
    @MapKey(name = "period")
    private Map<Integer, CompanyPeriodData> periodData = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(id, company.id) &&
                Objects.equals(player, company.player) &&
                Objects.equals(name, company.name) &&
                Objects.equals(periodData, company.periodData);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public CompanyPeriodData getDataByPeriod(int period) {
        return periodData.get(period);
    }

    public void addDataByPeriod(int period, CompanyPeriodData newPeriodCompany) {
        this.periodData.put(period, newPeriodCompany);
    }
}
