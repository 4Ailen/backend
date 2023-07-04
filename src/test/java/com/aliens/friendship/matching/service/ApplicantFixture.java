package com.aliens.friendship.matching.service;

import com.aliens.friendship.domain.matching.domain.Applicant;
import com.aliens.friendship.domain.matching.domain.Language;
import com.aliens.friendship.domain.member.domain.Member;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ApplicantFixture {
    private static Applicant createApplicant(Member member, Language firstLanguage, Language secondLanguage) {
        return Applicant.builder()
                .id(member.getId())
                .member(member)
                .firstPreferLanguage(firstLanguage)
                .secondPreferLanguage(secondLanguage)
                .applicationDate(getCurrentKoreanDateAsString())
                .build();
    }

    public static Applicant createTestApplicant(Member member) {
        return createApplicant(member, new Language(1, "Korean"), new Language(2, "English"));
    }

    private static String getCurrentKoreanDateAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        return sdf.format(new Date());
    }
}



