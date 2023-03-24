package com.aliens.friendship.matching.service.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Matching{
    private Participant partner;
    private Long chattingRoomId;
}
