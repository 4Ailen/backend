package com.aliens.friendship.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = ChattingRoom.TABLE_NAME, schema = "aliendb")
public class ChattingRoom {
    public static final String TABLE_NAME = "chatting_room";
    public static final String COLUMN_ID_NAME = "chatting_room_id";
    public static final String COLUMN_STATUS_NAME = "status";

    @Setter(AccessLevel.NONE)
    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @Column(name = COLUMN_STATUS_NAME, nullable = false, length = 45)
    private String status;

}