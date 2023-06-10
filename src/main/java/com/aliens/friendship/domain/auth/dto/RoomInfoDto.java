package com.aliens.friendship.domain.auth.dto;

import com.aliens.friendship.domain.chatting.domain.ChatMessage;
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
