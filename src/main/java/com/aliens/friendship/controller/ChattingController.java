package com.aliens.friendship.controller;

import com.aliens.friendship.domain.ChattingRoom;
import com.aliens.friendship.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<ChattingRoom> rooms = chatService.findAllRoom();
        Map<String, Object> result = new HashMap<>();
        result.put("chattingRooms",rooms);
        return ApiRes.SUCCESS(result);
    }
}
