package com.topmanager.oiltycoon.game.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Component
public class RoomSchedulingService {

    private Set<RoomRunnable> roomRunnableSet;

    public void addRoomRunnable(RoomRunnable roomRunnable) {
        roomRunnableSet.add(roomRunnable);
    }

    public void removeRoomRunnble(RoomRunnable roomRunnable) {
        roomRunnableSet.remove(roomRunnable);
    }

    @Scheduled(fixedDelayString = "${room-schedule-delay}")
    private void releaseSecond() {
        roomRunnableSet.forEach((RoomRunnable::roomUpdate));
    }
}
