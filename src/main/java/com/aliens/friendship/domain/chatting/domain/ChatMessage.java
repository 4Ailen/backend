package com.aliens.friendship.domain.chatting.domain;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RedisHash("ChatMessage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ChatMessage {

    @Id @GeneratedValue
    private Long id;

    @Indexed
    private Long room;

    private String sender;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String sendDate;

    private Integer category; // 0 일반,  1 vs 메시지, 2 차단된 상태입니다.

    @Builder
    public ChatMessage(Long room, String sender, Integer category, String message) {
        this.room = room;
        this.sender = sender;
        this.message = message;
        this.category = category;
        LocalDateTime now = LocalDateTime.now();
        this.sendDate =  now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }


    @Builder
    public static ChatMessage createChat(Long room, String sender, String message,Integer category) {
        return ChatMessage.builder()
                .room(room)
                .sender(sender)
                .category(category)
                .message(message)
                .build();
    }
}
