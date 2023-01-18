package com.aliens.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchedApplicants {
    int memberId1;
    int memberId2;
    Integer memberId3;

}
