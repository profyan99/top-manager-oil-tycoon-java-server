package com.topmanager.oiltycoon.game.model;

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

}
