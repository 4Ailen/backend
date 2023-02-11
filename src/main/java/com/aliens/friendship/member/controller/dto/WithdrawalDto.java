package com.aliens.friendship.member.controller.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalDto {
    private String email;
    private String password;
}
