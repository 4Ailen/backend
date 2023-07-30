package com.aliens.friendship.domain.applicant.controller.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantRequestDto {
    private String firstPreferLanguage;
    private String secondPreferLanguage;
}
