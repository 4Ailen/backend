package com.aliens.friendship.chatting.controller;

import com.aliens.friendship.chatting.controller.dto.RoomForm;
import com.aliens.friendship.chatting.domain.ChattingRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.aliens.friendship.chatting.domain.ChatMessage;
import com.aliens.friendship.chatting.service.ChattingService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RoomController {

    private final ChattingService chatService;

    /**
     * 채팅방 참여하기
     * @param roomId 채팅방 id
     */
    @GetMapping("/{roomId}")
    public String joinRoom(@PathVariable(required = false) Long roomId, Model model) {
        List<ChatMessage> chatList = chatService.findAllChatByRoomId(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("chatList", chatList);
        return "chat/room";
    }

    /**
     * 채팅방 등록
     * @param form
     */
    @PostMapping("/room")
    public String createRoom(RoomForm form) {
        chatService.createRoom();
        return "redirect:/roomList";
    }

    /**
     * 채팅방 리스트 보기
     */
    @GetMapping("/roomList")
    public String roomList(Model model) {
        List<ChattingRoom> roomList = chatService.findAllRoom();
        model.addAttribute("roomList", roomList);
        return "chat/roomList";
    }

    /**
     * 방만들기 폼
     */
    @GetMapping("/roomForm")
    public String roomForm() {
        return "chat/roomForm";
    }

}
