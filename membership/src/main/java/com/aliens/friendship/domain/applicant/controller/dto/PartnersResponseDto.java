package com.aliens.friendship.domain.applicant.controller.dto;

import com.aliens.db.member.entity.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PartnersResponseDto {
    public List<Partner> partners;

    public PartnersResponseDto() {
        this.partners = new ArrayList<>();
    }

    @Getter
    @Builder
    public static class Partner {
        private String roomState;
        private Long roomId;
        private String name;
        private String nationality;
        private String gender;
        private MemberEntity.Mbti mbti;
        private Long memberId;
        private String profileImage;
        private String firstPreferLanguage;
        private String secondPreferLanguage;
        private String selfIntroduction;
    }
}
