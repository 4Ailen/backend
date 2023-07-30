package com.aliens.friendship.domain.applicant.controller.dto;

import com.aliens.db.member.entity.MemberEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicantResponseDto {

    private ApplicantResponseDto.Member member;
    private ApplicantResponseDto.PreferLanguages preferLanguages;

    @Getter
    @Builder
    public static class Member {
        private String name;
        private String gender;
        private MemberEntity.Mbti mbti;
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
