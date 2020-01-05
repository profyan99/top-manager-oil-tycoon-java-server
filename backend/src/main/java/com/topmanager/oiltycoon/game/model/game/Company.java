package com.topmanager.oiltycoon.game.model.game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.topmanager.oiltycoon.game.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Embedded
    @JsonManagedReference
    private Store store;

    private int bank;
    private int rating;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return bank == company.bank &&
                rating == company.rating &&
                Objects.equals(id, company.id) &&
                Objects.equals(player, company.player) &&
                Objects.equals(name, company.name) &&
                Objects.equals(store, company.store);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
