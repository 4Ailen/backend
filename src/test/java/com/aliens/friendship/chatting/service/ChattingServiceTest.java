package com.aliens.friendship.chatting.service;

import com.aliens.friendship.chatting.domain.ChatMessage;
import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.chatting.repository.ChatMessageRepository;
import com.aliens.friendship.chatting.repository.ChattingRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChattingServiceTest {

    @Autowired
    ChattingRoomRepository chattingRoomRepository;

    @Autowired
    ChattingService chattingService;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Test
    void ChatMessageSaveTestWithRedis() {
        ChattingRoom room = chattingService.createRoom();
        ChatMessage chat = ChatMessage.createChat(room.getId(), "Aden", "안녕하세요",1);
        chattingService.saveChatMessage(room.getId(), chat.getSender(), chat.getMessage(), chat.getCategory());

        List<ChatMessage> chatMessages = chatMessageRepository.findAllByRoom(room.getId());
        assertEquals(chatMessages.get(0).getMessage(),"안녕하세요");
        assertEquals(chatMessages.get(0).getSender(),"Aden");
        assertEquals(chatMessages.get(0).getCategory(),1);
    }
}