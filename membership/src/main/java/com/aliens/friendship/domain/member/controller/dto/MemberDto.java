package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.db.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberDto {
    private Long memberId;
    private String name;
    private String profileImageUrl;

    public static MemberDto from(MemberEntity member) {
        return new MemberDto(
                member.getId(),
                member.getName(),
                member.getProfileImageUrl()
        );
    }
}