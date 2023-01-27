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
    private int memberId1;
    private int memberId2;
    private int memberId3;
    private Integer memberId4;
    private Integer memberId5;

    public void setMemberId5(Integer memberId5) {
        this.memberId5 = memberId5;
    }
}
