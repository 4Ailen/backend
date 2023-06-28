package com.aliens.friendship.matching.controller;

import com.aliens.friendship.domain.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.domain.member.domain.Member;

import java.util.ArrayList;
import java.util.List;

public class PartnersFixture {

    public static List<PartnersResponse.Member> createPartners(int count) {
        List<PartnersResponse.Member> partners = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            PartnersResponse.Member partner = PartnersResponse.Member.builder()
                    .memberId(i)
                    .name("Partner" + i)
                    .mbti(Member.Mbti.INTJ)
                    .gender("GENDER" + i)
                    .nationality("Nationality" + i)
                    .firstPreferLanguage("Korean")
                    .secondPreferLanguage("Chinese")
                    .profileImage("ProfileImageURL" + i)
                    .build();
            partners.add(partner);
        }

        return partners;
    }
}
