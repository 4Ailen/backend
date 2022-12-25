package com.aliens.friendship.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MatchedApplicants {
    int memberId1;
    int memberId2;
    Integer memberId3;

    @Builder
    public MatchedApplicants(int memberId1, int memberId2, Integer memberId3) {
        this.memberId1 = memberId1;
        this.memberId2 = memberId2;
        this.memberId3 = memberId3;
    }
}
