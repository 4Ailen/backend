package com.aliens.friendship.chatting.controller;
import com.aliens.friendship.chatting.controller.dto.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.aliens.friendship.chatting.domain.ChatMessage;
import com.aliens.friendship.chatting.service.ChattingService;

import java.time.LocalDateTime;


@Controller
@RequiredArgsConstructor
public class MessageController {

    private final ChattingService chatService;

    @MessageMapping("/{roomId}")
    @SendTo("/room/{roomId}")
    public ChatMessageRequest MessagingInterceptToSave(@DestinationVariable Long roomId, ChatMessageRequest message) {
        //채팅 저장
        ChatMessage chat = chatService.saveChatMessage(roomId, message.getSender(), message.getMessage(),0);
        return ChatMessageRequest.builder()
                .roomId(roomId)
                .sender(chat.getSender())
                .message(chat.getMessage())
                .category(0)
                .sendDate(LocalDateTime.now())
                .build();
    }

    @MessageMapping("/{roomId}/vsMessage")
    @SendTo("/room/{roomId}")
    public ChatMessageRequest MessagingInterceptToSaveVsQuestion(@DestinationVariable Long roomId, ChatMessageRequest message) {
        //질문 채팅 저장
        ChatMessage chat = chatService.saveChatMessage(roomId, message.getSender(), message.getMessage(),1);
        return ChatMessageRequest.builder()
                .roomId(roomId)
                .sender(chat.getSender())
                .message(chat.getMessage())
                .category(1)
                .sendDate(LocalDateTime.now())
                .build();
    }
}
