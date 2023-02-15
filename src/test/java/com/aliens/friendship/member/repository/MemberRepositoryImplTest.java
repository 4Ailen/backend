package com.aliens.friendship.member.repository;

import com.aliens.friendship.jwt.domain.Authority;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryImplTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    @Test
    @DisplayName("이메일로 멤버와 권한 정보 함께 반환 성공")
    void GetMemberWithAuthority_Success_When_GivenEmail() throws Exception {
        //given: 회원 정보 생성 후 회원가입
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        memberService.join(mockJoinDto);

        //when: 이메일로 member와 authority 정보 함께 반환
        Member member = memberRepository.findByUsernameWithAuthority("test@case.com").orElseThrow(() -> new NoSuchElementException("없는 회원입니다."));

        //then
        //member의 정보와 mockJoinDto의 정보 일치 확인
        assertThat(member.toString()).contains(mockJoinDto.getEmail())
                .contains(mockJoinDto.getPassword())
                .contains(mockJoinDto.getMbti())
                .contains(mockJoinDto.getNationality().toString())
                .contains(mockJoinDto.getBirthday())
                .contains(mockJoinDto.getName())
                .contains(mockJoinDto.getImageUrl());
        //member의 authorities 정보 확인
        Set<Authority> memberAuthorities = member.getAuthorities();
        assertEquals(memberAuthorities.size(), 1);
        Iterator<Authority> it = memberAuthorities.iterator();
        while (it.hasNext()) {
            Authority authority = it.next();
            assertEquals(authority.getMember().getEmail(), "test@case.com");
            assertEquals(authority.getAuthority(), "ROLE_USER");
        }
    }

    private JoinDto createMockJoinDto(String email, String password) {
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        return JoinDto.builder()
                .email(email)
                .password(password)
                .name("Joy")
                .mbti("ISFJ")
                .gender("FEMALE")
                .nationality(new Nationality(1, "South Korea"))
                .birthday("1993-12-31")
                .image(mockMultipartFile)
                .build();
    }
}