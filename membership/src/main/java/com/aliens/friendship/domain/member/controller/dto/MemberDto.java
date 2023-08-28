package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.db.member.entity.MemberEntity;

public class MemberDto {

    private Long memberId;
    private String email;
    private String name;
    private String profileImageUrl;
    private String nationality;

    private MemberDto(
            Long memberId,
            String email,
            String name,
            String profileImageUrl,
            String nationality
    ) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.nationality = nationality;
    }

    public static MemberDto of(
            Long memberId,
            String email,
            String nickname,
            String profileImageUrl,
            String nationality
    ) {
        return new MemberDto(
                memberId,
                email,
                nickname,
                profileImageUrl,
                nationality
        );
    }

    public static MemberDto from(MemberEntity memberEntity) {
        return new MemberDto(
                memberEntity.getId(),
                memberEntity.getEmail(),
                memberEntity.getName(),
                memberEntity.getProfileImageUrl(),
                memberEntity.getNationality()
        );
    }
}