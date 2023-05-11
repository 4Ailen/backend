package com.aliens.friendship.domain.member.controller.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordUpdateRequestDto {
    private String currentPassword;
    private String newPassword;
}
