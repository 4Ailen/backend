package com.aliens.friendship.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "chatting_room", schema = "aliendb")
public class ChattingRoom {
    @Id
    @Column(name = "chatting_room_id", nullable = false)
    private Integer id;

    @Column(name = "status", nullable = false, length = 45)
    private String status;

}