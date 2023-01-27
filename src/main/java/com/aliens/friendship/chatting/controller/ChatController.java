package com.aliens.friendship.chatting.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.aliens.friendship.chatting.domain.ChatMessage;
import com.aliens.friendship.chatting.service.ChattingService;


@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChattingService chatService;

    @MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public com.aliens.friendship.chatting.controller.dto.ChatMessage test(@DestinationVariable Integer roomId, com.aliens.friendship.chatting.controller.dto.ChatMessage message) {

        //채팅 저장
        ChatMessage chat = chatService.createChat(roomId, message.getSender(), message.getMessage());
        return com.aliens.friendship.chatting.controller.dto.ChatMessage.builder()
                .roomId(roomId)
                .sender(chat.getSender())
                .message(chat.getMessage())
                .build();
    }

}
