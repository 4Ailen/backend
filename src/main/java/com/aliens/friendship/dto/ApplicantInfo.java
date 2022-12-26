package com.aliens.friendship.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApplicantInfo {

    private int memberId;
    private int question; // 1 또는 2 값
    private int language;

    @Builder
    public ApplicantInfo(int memberId, int question, int language) {
        this.memberId = memberId;
        this.question = question;
        this.language = language;
    }
}
