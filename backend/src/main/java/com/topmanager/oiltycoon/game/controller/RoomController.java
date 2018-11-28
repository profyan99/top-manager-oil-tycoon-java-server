package com.topmanager.oiltycoon.game.controller;

import com.topmanager.oiltycoon.game.dto.request.RoomAddDto;
import com.topmanager.oiltycoon.game.dto.request.RoomConnectDto;
import com.topmanager.oiltycoon.game.dto.response.RoomInfoDto;
import com.topmanager.oiltycoon.game.service.RoomService;
import com.topmanager.oiltycoon.social.security.annotation.IsAdmin;
import com.topmanager.oiltycoon.social.security.annotation.IsPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static com.topmanager.oiltycoon.game.model.RoomDestination.*;

@Controller
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    private RoomService roomService;

    @Autowired
    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @SubscribeMapping(ROOM_LIST)
    public List<RoomInfoDto> roomList(Principal p) {
        logger.debug("Subscribe to roomList: "+p.getName());
        return roomService.getRoomsList();
    }

    @IsPlayer
    @PostMapping(path = "/api"+ROOM_ADD, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRoom(@RequestBody @Valid RoomAddDto roomAddDto) {
        roomService.addRoom(roomAddDto);
        return ResponseEntity.ok().build();
    }

    @IsPlayer
    @PostMapping(path = "/api"+ROOM_USER_CONNECT)
    public ResponseEntity<?> connectToRoom(@RequestBody @Valid RoomConnectDto roomConnectDto) {
        return ResponseEntity.ok(roomService.connectToRoom(roomConnectDto));
    }

    @IsAdmin
    @DeleteMapping(path = "/api"+ROOM_DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRoom(@RequestParam int roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok().build();
    }


}
