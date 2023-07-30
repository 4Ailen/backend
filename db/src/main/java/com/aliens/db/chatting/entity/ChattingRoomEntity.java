package com.aliens.db.chatting.entity;

import com.aliens.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;


@NoArgsConstructor @AllArgsConstructor
@Entity @Getter  @SuperBuilder @ToString
@Table(name = "chattingroom", schema = "aliendb")
public class ChattingRoomEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 45)
    private RoomStatus status = RoomStatus.OPEN;

    public void updateStatus(RoomStatus status) {
        this.status = status;
    }

    public enum RoomStatus {
        OPEN, CLOSE;
    }

}