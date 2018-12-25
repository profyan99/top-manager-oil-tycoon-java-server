package com.topmanager.oiltycoon.game.model;

import com.topmanager.oiltycoon.social.dto.UserDto;
import com.topmanager.oiltycoon.social.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private long timeEndReload;
    private boolean connected;
    private String userName;

    @ManyToOne
    @JoinColumn(name="room_id", nullable=false)
    private Room room;

    public Player(User user) {
        this.user = user;
        this.userName = user.getUserName();
    }
}
