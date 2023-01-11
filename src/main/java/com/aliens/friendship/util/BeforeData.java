package com.aliens.friendship.util;

import com.aliens.friendship.domain.dto.JoinDto;
import com.aliens.friendship.service.MemberService;
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
        // 테스트 용 ID
        JoinDto testJoin = JoinDto.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("test")
                .build();
        memberService.join(testJoin);

        // admin 아이디 미리 기입
        JoinDto adminJoin = JoinDto.builder()
                .email("admin@admin.com")
                .password("1234")
                .nickname("admin")
                .build();
        memberService.joinAdmin(adminJoin);
    }
}