package com.aliens.friendship.controller;

import com.aliens.friendship.domain.ChattingRoom;
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

    //Todo: 해당 멤버를 기준으로 채팅방 목록을 가져와야함
    @GetMapping("/chat/rooms")
    public ApiRes<Map<String, Object>> getRooms(){
        List<ChattingRoom> rooms = chatService.findAllRoom();
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
