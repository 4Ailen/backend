package com.aliens.friendship.domain;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import com.aliens.friendship.dto.ChatMessage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Room {
    @Id
    @GeneratedValue
    @Column(name = "room_id")
    private Long id;
    private String name;

    @Builder
    public Room(String name) {
        this.name = name;
    }

    /**
     * 채팅방 생성
     * @param name 방 이름
     * @return Room Entity
     */
    public static Room createRoom(String name) {
        return Room.builder()
                .name(name)
                .build();
    }

}