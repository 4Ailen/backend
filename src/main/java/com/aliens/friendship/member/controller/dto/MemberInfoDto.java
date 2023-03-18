package com.aliens.friendship.member.controller.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoDto {

    private String email;
    private String mbti;
    private String gender;
    private String nationality;
    private String birthday;
    private String name;
    private String profileImage;
    private int age;
}
