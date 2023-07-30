package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.db.member.entity.MemberEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoResponseDto {
    private Long memberId;
    private String email;
    private MemberEntity.Mbti mbti;
    private String gender;
    private String nationality;
    private String birthday;
    private String name;
    private String profileImage;
    private String selfIntroduction;
    private int age;
}
