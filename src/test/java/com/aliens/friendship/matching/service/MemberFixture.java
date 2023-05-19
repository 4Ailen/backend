package com.aliens.friendship.matching.service;

import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.domain.Nationality;

import java.time.Instant;
import java.util.HashSet;

public class MemberFixture {
    private static Member createMember(Integer id, String email, String password, Member.Mbti mbti, String gender,
                                       Nationality nationality, String birthday, String name, Instant joinDate,
                                       String profileImageUrl, Byte notificationStatus, Member.Status isApplied) {
        return Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .mbti(mbti)
                .gender(gender)
                .nationality(nationality)
                .birthday(birthday)
                .name(name)
                .joinDate(joinDate)
                .profileImageUrl(profileImageUrl)
                .notificationStatus(notificationStatus)
                .status(isApplied)
                .authorities(new HashSet<>())
                .build();
    }

    public static Member createTestMember() {
        Nationality nationality = Nationality.builder()
                .id(1)
                .nationalityText("Korean")
                .build();
        return createMember(1, "test@test.com", "password", Member.Mbti.ENFP, "F", nationality, "2000-01-01", "Test User",
                Instant.now(), "/default_image.jpg", (byte) 0, Member.Status.NOT_APPLIED);
    }

    public static Member createTestMember(Integer id, String email) {
        Nationality nationality = Nationality.builder()
                .id(1)
                .nationalityText("Korean")
                .build();
        return createMember(id, email, "password", Member.Mbti.ENFP, "F", nationality, "2000-01-01", "Test User",
                Instant.now(), "/default_image.jpg", (byte) 0, Member.Status.NOT_APPLIED);
    }

    public static Member createTestMember(Member.Status isApplied) {
        Nationality nationality = Nationality.builder()
                .id(1)
                .nationalityText("Korean")
                .build();
        return createMember(1, "test@test.com", "password", Member.Mbti.ENFP, "F", nationality, "2000-01-01", "Test User",
                Instant.now(), "/default_image.jpg", (byte) 0, isApplied);
    }
}
