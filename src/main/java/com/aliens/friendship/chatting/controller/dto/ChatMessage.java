package com.aliens.friendship.chatting.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private Long roomId;
    private String sender;
    private String message;
    private LocalDateTime sendDate;
    private Integer category;
}
