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

    private RoomDao roomDao;
    private RoomRunnable roomRunnable;

    @Autowired
    public RoomSchedulingService(RoomRunnable roomRunnable, RoomDao roomDao) {
        this.roomRunnable = roomRunnable;
        this.roomDao = roomDao;
    }

    @Scheduled(fixedDelayString = "${room-schedule-delay}")
    @Transactional
    public void releaseSecond() {
        roomDao.findAll().forEach(room -> roomRunnable.roomUpdate(room));
    }
}
