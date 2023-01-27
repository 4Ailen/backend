package com.aliens.friendship.jwt.domain.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfoDto {
    private int roomId;
    private String status;
    private int partnerId;
}
