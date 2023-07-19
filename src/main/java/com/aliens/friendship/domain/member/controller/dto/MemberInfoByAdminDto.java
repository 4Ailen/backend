package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.friendship.domain.member.domain.Member;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoByAdminDto {
    private String email;
    private Member.Mbti mbti;
    private String gender;
    private String nationality;
    private Instant joinDate;
    private Member.Status status;
    private String name;
    private String withdrawalDate;
    private String birthday;
    private int age;
}
