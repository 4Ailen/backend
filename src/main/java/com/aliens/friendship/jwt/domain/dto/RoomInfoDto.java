package com.aliens.friendship.jwt.domain.dto;

import com.aliens.friendship.chatting.domain.ChatMessage;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfoDto {
    private Long roomId;
    private String status;
    private List<ChatMessage> chatMessages;
    private Integer partnerId;
}
