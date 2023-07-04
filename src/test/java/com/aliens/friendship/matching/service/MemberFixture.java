package com.aliens.friendship.matching.service;

import com.aliens.friendship.domain.member.domain.Member;

import java.time.Instant;
import java.util.HashSet;

public class MemberFixture {
    private static Member createMember(Integer id, String email, String password, Member.Mbti mbti, String gender,
                                       String nationality, String birthday, String name, Instant joinDate,
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
        return createMember(1, "test@test.com", "password", Member.Mbti.ENFP, "F", "Korean", "2000-01-01", "Test User",
                Instant.now(), "/default_image.jpg", (byte) 0, Member.Status.NOT_APPLIED);
    }

    public static Member createTestMember(Integer id, String email) {
        return createMember(id, email, "password", Member.Mbti.ENFP, "F", "Korean", "2000-01-01", "Test User",
                Instant.now(), "/default_image.jpg", (byte) 0, Member.Status.NOT_APPLIED);
    }

    public static Member createTestMember(Member.Status isApplied) {
        return createMember(1, "test@test.com", "password", Member.Mbti.ENFP, "F", "Korean", "2000-01-01", "Test User",
                Instant.now(), "/default_image.jpg", (byte) 0, isApplied);
    }
}
