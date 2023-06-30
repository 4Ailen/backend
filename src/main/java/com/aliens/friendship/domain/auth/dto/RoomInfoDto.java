package com.aliens.friendship.domain.auth.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfoDto {
    private Long roomId;
    private String status;
    private Integer partnerId;

}
