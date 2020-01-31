package com.topmanager.oiltycoon.game.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.topmanager.oiltycoon.game.model.game.Company;
import com.topmanager.oiltycoon.social.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    private long timeEndReload;
    private boolean connected;
    private String userName;
    private PlayerState state;

    @OneToOne(
            mappedBy = "player",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public Player(User user) {
        this.user = user;
        this.userName = user.getUserName();
        user.setPlayer(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Player))
            return false;

        return id != null && id.equals(((Player) o).getId());
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    public Player(User user, long timeEndReload, boolean connected, String userName, Company company, Room room) {
        this.user = user;
        this.timeEndReload = timeEndReload;
        this.connected = connected;
        this.userName = userName;
        this.state = PlayerState.WAIT;
        this.company = company;
        this.room = room;
    }
}
