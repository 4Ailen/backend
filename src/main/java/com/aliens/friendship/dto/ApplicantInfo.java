package com.aliens.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantInfo {

    private int memberId;
    private int answer; // 1 또는 2 값
    private int language;
}
