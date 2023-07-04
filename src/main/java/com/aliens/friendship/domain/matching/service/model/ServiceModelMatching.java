package com.aliens.friendship.domain.matching.service.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceModelMatching {
    private Participant partner;
    private Long chattingRoomId;
}