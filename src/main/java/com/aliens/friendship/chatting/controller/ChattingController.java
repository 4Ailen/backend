package com.aliens.friendship.chatting.controller;

import com.aliens.friendship.jwt.domain.dto.RoomInfoDto;
import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.chatting.service.ChattingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChattingController {
    private ChattingService chatService;

    @Autowired
    public ChattingController(ChattingService chatService){
        this.chatService = chatService;
    }


    @GetMapping("/chat/rooms")
    public Response<Map<String, Object>> getRooms(){
        //Todo: jwt를 통해 접속된 멤버 아이디를 가져와야함
        Integer memberId = 10;
        List<RoomInfoDto> rooms = chatService.getRoomInfoDtoListByMatchingParticipantId(memberId);
        Map<String, Object> result = new HashMap<>();
        result.put("chattingRooms",rooms);
        return Response.SUCCESS(result);
    }

    @PatchMapping("/chat/room/{roomId}/status/{status}")
    public Response<String> updateRoomStatus(@PathVariable int roomId, @PathVariable String status){
        chatService.updateRoomStatus(roomId, status);
        return Response.SUCCESS("Room status updated");
    }
}
