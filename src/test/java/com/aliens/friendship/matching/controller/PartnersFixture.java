package com.aliens.friendship.matching.controller;

import com.aliens.friendship.matching.controller.dto.PartnersResponse;

import java.util.ArrayList;
import java.util.List;

public class PartnersFixture {

    public static List<PartnersResponse.Member> createPartners(int count) {
        List<PartnersResponse.Member> partners = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            PartnersResponse.Member partner = PartnersResponse.Member.builder()
                    .memberId(i)
                    .name("Partner" + i)
                    .mbti("MBTI" + i)
                    .gender("GENDER" + i)
                    .nationality("Nationality" + i)
                    .countryImage("CountryImageURL" + i)
                    .profileImage("ProfileImageURL" + i)
                    .build();
            partners.add(partner);
        }

        return partners;
    }
}
