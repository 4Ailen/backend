package com.aliens.friendship.controller;

import com.aliens.friendship.domain.ChattingRoom;
import com.aliens.friendship.domain.dto.RoomInfoDto;
import com.aliens.friendship.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChattingController {
    private ChatService chatService;

    @Autowired
    public ChattingController(ChatService chatService){
        this.chatService = chatService;
    }


    @GetMapping("/chat/rooms")
    public ApiRes<Map<String, Object>> getRooms(){
        //Todo: jwt를 통해 접속된 멤버 아이디를 가져와야함
        Integer memberId = 10;
        List<RoomInfoDto> rooms = chatService.getRoomInfoDtoListByMatchingParticipantId(memberId);
        Map<String, Object> result = new HashMap<>();
        result.put("chattingRooms",rooms);
        return ApiRes.SUCCESS(result);
    }

    @PatchMapping("/chat/room/{roomId}/status/{status}")
    public ApiRes<String> updateRoomStatus(@PathVariable int roomId, @PathVariable String status){
        chatService.updateRoomStatus(roomId, status);
        return ApiRes.SUCCESS("Room status updated");
    }
}
