package com.aliens.friendship.domain.matching.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicantResponse {

    private ApplicantResponse.Member member;
    private ApplicantResponse.PreferLanguages preferLanguages;

    @Getter
    @Builder
    public static class Member {
        private String name;
        private String gender;
        private String mbti;
        private String nationality;
        private Integer age;
        private String profileImage;
        private String countryImage;
    }

    @Getter
    @Builder
    public static class PreferLanguages{
        private String firstPreferLanguage;
        private String secondPreferLanguage;
    }

}