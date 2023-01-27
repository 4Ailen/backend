package com.aliens.friendship.chatting.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.*;
import java.time.LocalDateTime;

@RedisHash("ChatMessage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ChatMessage {

    @Id @GeneratedValue
    private Long id;

    @Indexed
    private Integer room;

    private String sender;

    @Column(columnDefinition = "TEXT")
    private String message;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime sendDate;

    @Builder
    public ChatMessage(Integer room, String sender, String message) {
        this.room = room;
        this.sender = sender;
        this.message = message;
        this.sendDate = LocalDateTime.now();
    }

    /**
     * 채팅 생성
     * @param room 채팅 방
     * @param sender 보낸이
     * @param message 내용
     * @return Chat Entity
     */
    public static ChatMessage createChat(Integer room, String sender, String message) {
        return ChatMessage.builder()
                .room(room)
                .sender(sender)
                .message(message)
                .build();
    }
}
