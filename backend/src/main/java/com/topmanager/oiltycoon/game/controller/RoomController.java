package com.topmanager.oiltycoon.game.controller;

import com.topmanager.oiltycoon.game.dto.request.ChatMessageRequestDto;
import com.topmanager.oiltycoon.game.dto.request.RoomAddDto;
import com.topmanager.oiltycoon.game.dto.request.RoomChatMessageRequestDto;
import com.topmanager.oiltycoon.game.dto.request.RoomConnectDto;
import com.topmanager.oiltycoon.game.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.game.service.impl.RoomListService;
import com.topmanager.oiltycoon.game.service.impl.RoomService;
import com.topmanager.oiltycoon.social.dto.ErrorDto;
import com.topmanager.oiltycoon.social.security.annotation.IsAdmin;
import com.topmanager.oiltycoon.social.security.annotation.IsPlayer;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    private final RoomService roomService;
    private final RoomListService roomListService;

    @Autowired
    public RoomController(RoomService roomService, RoomListService roomListService) {
        this.roomService = roomService;
        this.roomListService = roomListService;
    }

    @IsPlayer
    @PostMapping(path = "/api/room", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRoom(@RequestBody @Valid RoomAddDto roomAddDto) {
        roomService.addRoom(roomAddDto);
        return ResponseEntity.ok().build();
    }

    @IsAdmin
    @DeleteMapping(path = "/api/room/{roomId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRoom(@PathVariable int roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok().build();
    }

    @IsPlayer
    @PostMapping(path = "/api/room/{roomId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> connectRoom(@RequestBody @Valid RoomConnectDto connectDto, @PathVariable int roomId) {
        return ResponseEntity.ok(roomService.connectToRoom(roomId, connectDto));
    }

    @IsPlayer
    @PostMapping(path = "/api/room/{roomId}/message", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendChatRoomMessage(@RequestBody @Valid RoomChatMessageRequestDto requestDto,
                                                 @PathVariable int roomId) {
        requestDto.setRoomId(roomId);
        roomService.sendChatMessage(requestDto);
        return ResponseEntity.ok().build();
    }

    @IsPlayer
    @GetMapping(path = "/api/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRoomList() {
        return ResponseEntity.ok(roomListService.getRoomsList());
    }

    @IsPlayer
    @PostMapping(path = "/api/rooms/message", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> send(@RequestBody @Valid ChatMessageRequestDto requestDto) {
        roomListService.sendChatMessage(requestDto);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/room/{roomId}/connect")
    public void connectRoomWebsocket(@DestinationVariable int roomId,
                                     Authentication authentication,
                                     SimpMessageHeaderAccessor accessor) {
        logger.debug(":: Connect via websocket [" + roomId + "] by " + authentication.getName());
        roomService.connectToRoomWebsocket(roomId, accessor);
    }

    @MessageExceptionHandler
    @SendToUser(destinations = "/topic/errors")
    public ErrorResponseDto handleException(Exception exception) {
        logger.debug(":: Handle exception in websocket controller: " + exception.getMessage());
        if (exception instanceof RestException) {
            RestException restException = (RestException) exception;
            return new ErrorResponseDto(new ErrorDto(restException.getErrorCode().name(), restException.getMessage()));
        } else {
            return new ErrorResponseDto(new ErrorDto(
                    ErrorCode.ERROR_WITH_DATABASE.name(),
                    ErrorCode.ERROR_WITH_DATABASE.getMessage()
            ));
        }
    }

}
