package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.friendship.domain.member.domain.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoDto {
    private Integer memberId;
    private String email;
    private Member.Mbti mbti;
    private String gender;
    private String nationality;
    private String birthday;
    private String name;
    private String profileImage;
    private int age;
}
