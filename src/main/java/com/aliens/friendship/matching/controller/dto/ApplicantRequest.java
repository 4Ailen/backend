package com.aliens.friendship.matching.controller.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantRequest {
    private int firstPreferLanguage;
    private int secondPreferLanguage;
}
