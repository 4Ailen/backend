package com.aliens.friendship.matching.service;

import com.aliens.friendship.matching.domain.Applicant;
import com.aliens.friendship.matching.domain.Language;
import com.aliens.friendship.member.domain.Member;

public class ApplicantFixture {
    private static Applicant createApplicant(Member member, Language firstLanguage, Language secondLanguage) {
        return Applicant.builder()
                .id(member.getId())
                .member(member)
                .firstPreferLanguage(firstLanguage)
                .secondPreferLanguage(secondLanguage)
                .build();
    }

    public static Applicant createTestApplicant(Member member) {
        return createApplicant(member, new Language(1, "Korean"), new Language(2, "English"));
    }
}



