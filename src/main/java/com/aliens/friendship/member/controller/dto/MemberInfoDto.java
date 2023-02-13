package com.aliens.friendship.member.controller.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoDto {
    private int memberId;
    private String email;
    private String mbti;
    private String gender;
    private int nationality;
    private int age;
    private String birthday;
    private String name;
}
