package com.aliens.friendship.domain.matching.controller.dto;

import com.aliens.friendship.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PartnersResponse {
    private List<Member> partners;

    public PartnersResponse() {
        this.partners = new ArrayList<>();
    }

    @Getter
    @Builder
    public static class Member {
        private Integer memberId;
        private String name;
        private com.aliens.friendship.domain.member.domain.Member.Mbti mbti;
        private String gender;
        private String nationality;
        private String profileImage;
        private String firstPreferLanguage;
        private String secondPreferLanguage;

        // Todo: 자기소개 추가
    }
}
