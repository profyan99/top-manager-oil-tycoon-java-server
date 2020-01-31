package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.service.RoomRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("singleton")
public class RoomSchedulingService {
    private static final Logger logger = LoggerFactory.getLogger(RoomSchedulingService.class);

    private final RoomDao roomDao;
    private final RoomRunnable roomRunnable;
    private final RoomService roomService;

    @Autowired
    public RoomSchedulingService(RoomRunnable roomRunnable, RoomDao roomDao, RoomService roomService) {
        this.roomRunnable = roomRunnable;
        this.roomDao = roomDao;
        this.roomService = roomService;
    }

    @Scheduled(fixedDelayString = "${room-schedule-delay}")
    @Transactional
    public void releaseSecond() {
        roomDao.findAll().forEach(room -> {
            if (room.getCurrentPlayers() == 0
                    && room.getPlayers().isEmpty()
                    && room.getPrepareSecond() <= 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + room.getName() + "] :: " + "no players in room. Delete room");
                }
                roomService.deleteRoom(room.getId());
            } else {
                if (roomRunnable.roomUpdate(room)) {
                    roomService.updateRoom(room);
                }
            }
        });
    }
}
