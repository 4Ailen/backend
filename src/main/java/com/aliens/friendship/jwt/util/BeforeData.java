package com.aliens.friendship.jwt.util;

import com.aliens.friendship.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class BeforeData implements CommandLineRunner {

    private final MemberService memberService;

    @Override
    public void run(String... args) throws Exception {
//        JoinDto testJoin = JoinDto.builder()
//                .email("test@test.com")
//                .password("1234")
//                .name("test")
//                .age(20)
//                .gender("MALE")
//                .mbti("ENFJ")
//                .nationality(Nationality.builder().id(1).build())
//                .build();
//        memberService.join(testJoin);
//
//        // admin 아이디 미리 기입
//        JoinDto adminJoin = JoinDto.builder()
//                .email("testAdmin@test.com")
//                .password("1234")
//                .name("test")
//                .age(20)
//                .gender("MALE")
//                .mbti("ENFJ")
//                .nationality(Nationality.builder().id(1).build())
//                .build();
//        memberService.joinAdmin(adminJoin);
    }
}