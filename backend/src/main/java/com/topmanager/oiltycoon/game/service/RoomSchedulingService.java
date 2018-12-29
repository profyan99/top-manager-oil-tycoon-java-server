package com.topmanager.oiltycoon.game.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@AllArgsConstructor
@Component
public class RoomSchedulingService {

    private final Set<RoomRunnable> roomRunnableSet;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
    private static final Logger logger = LoggerFactory.getLogger(RoomSchedulingService.class);

    public RoomSchedulingService() {
        roomRunnableSet = new HashSet<>();
    }

    public void addRoomRunnable(RoomRunnable roomRunnable) {
        writeLock.lock();
        try {
            logger.info(":: Try to get write lock [ADD]");
            roomRunnableSet.add(roomRunnable);
        } finally {
            writeLock.unlock();
            logger.info(":: Release write lock [ADD]");
        }
    }

    public void removeRoomRunnable(RoomRunnable roomRunnable) {
        writeLock.lock();
        try {
            logger.info(":: Try to get write lock [REMOVE]");
            roomRunnableSet.remove(roomRunnable);
        } finally {
            writeLock.unlock();
            logger.info(":: Release write lock [REMOVE]");
        }

    }

    @Scheduled(fixedDelayString = "${room-schedule-delay}")
    private void releaseSecond() {
        readLock.lock();
        try {
            logger.info(":: Try to get read lock");
            roomRunnableSet.forEach((RoomRunnable::roomUpdate));
        } finally {
            readLock.unlock();
            logger.info(":: Release read lock");
        }
    }
}
