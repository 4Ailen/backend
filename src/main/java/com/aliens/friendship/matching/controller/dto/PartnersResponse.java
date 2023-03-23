package com.aliens.friendship.matching.controller.dto;

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
        private String mbti;
        private String gender;
        private String nationality;
        private String profileImage;
        private String countryImage;
    }
}
