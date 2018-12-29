package com.topmanager.oiltycoon.game.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;

@AllArgsConstructor
@Component
@Scope("singleton")
public class RoomSchedulingService {

    private final CopyOnWriteArraySet<RoomRunnable> roomRunnableSet;
    private static final Logger logger = LoggerFactory.getLogger(RoomSchedulingService.class);

    public RoomSchedulingService() {
        roomRunnableSet = new CopyOnWriteArraySet<>();
    }

    public void addRoomRunnable(RoomRunnable roomRunnable) {
        roomRunnableSet.add(roomRunnable);
    }

    public void removeRoomRunnable(RoomRunnable roomRunnable) {
        roomRunnableSet.remove(roomRunnable);
    }

    @Scheduled(fixedDelayString = "${room-schedule-delay}")
    private void releaseSecond() {
        synchronized (roomRunnableSet) {
            roomRunnableSet.forEach((RoomRunnable::roomUpdate));
        }
    }
}
