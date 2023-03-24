package com.aliens.friendship.chatting.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = ChattingRoom.TABLE_NAME, schema = "aliendb")
public class ChattingRoom {
    public static final String TABLE_NAME = "chatting_room";
    public static final String COLUMN_ID_NAME = "chatting_room_id";
    public static final String COLUMN_STATUS_NAME = "status";

    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = COLUMN_STATUS_NAME, nullable = false, length = 45)
    private RoomStatus status = RoomStatus.PENDING;

    public void updateStatus(RoomStatus status) {
        this.status = status;
    }

    public enum RoomStatus {
        PENDING, OPEN, CLOSE;
    }

}