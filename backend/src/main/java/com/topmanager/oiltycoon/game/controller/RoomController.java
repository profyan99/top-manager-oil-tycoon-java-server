package com.topmanager.oiltycoon.game.controller;

import com.topmanager.oiltycoon.game.dto.request.RoomAddDto;
import com.topmanager.oiltycoon.game.dto.request.RoomConnectDto;
import com.topmanager.oiltycoon.game.dto.response.GameInfoDto;
import com.topmanager.oiltycoon.game.dto.response.RoomInfoDto;
import com.topmanager.oiltycoon.game.service.RoomService;
import com.topmanager.oiltycoon.social.dto.ErrorDto;
import com.topmanager.oiltycoon.social.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.social.security.annotation.IsAdmin;
import com.topmanager.oiltycoon.social.security.annotation.IsPlayer;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
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

    @IsPlayer
    @PostMapping(path = "/api" + ROOM_BASE_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRoom(@RequestBody @Valid RoomAddDto roomAddDto) {
        roomService.addRoom(roomAddDto);
        return ResponseEntity.ok().build();
    }

    @IsAdmin
    @DeleteMapping(path = "/api" + ROOM_BASE_ENDPOINT+"/{roomId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRoom(@PathVariable int roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok().build();
    }

    @SubscribeMapping(ROOM_LIST)
    public List<RoomInfoDto> roomList(Principal p) {
        logger.debug("Subscribe to roomList: " + p.getName());
        return roomService.getRoomsList();
    }

    @SubscribeMapping(ROOM_EVENT + "/{room}")
    public GameInfoDto connectRoom(@DestinationVariable int room,
                                            @Header(required = false, defaultValue = "") String password,
                                            Authentication authentication,
                                            SimpMessageHeaderAccessor accessor) {
        logger.debug(":: Subscribe event [" + ROOM_EVENT + "/" + room + "] by " + authentication.getName());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        GameInfoDto infoDto = roomService.connectToRoom(new RoomConnectDto(room, password), accessor);
        return infoDto;
    }

    @MessageExceptionHandler
    @SendToUser(destinations = "/queue/errors")
    public ErrorResponseDto handleException(RestException exception) {
        logger.debug(":: Handle rest exception in websocket controller: " + exception.getErrorCode().name());
        List<ErrorDto> errors = new ArrayList<>();
        errors.add(new ErrorDto(exception.getErrorCode().name(), exception.getMessage()));
        return new ErrorResponseDto(errors);
    }

}
