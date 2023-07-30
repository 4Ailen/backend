package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.db.member.entity.MemberEntity;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoByAdminResponseDto {
    private String email;
    private MemberEntity.Mbti mbti;
    private String gender;
    private String nationality;
    private Instant joinDate;
    private MemberEntity.Status status;
    private String name;
    private String withdrawalAt;
    private String birthday;
    private int age;
}
