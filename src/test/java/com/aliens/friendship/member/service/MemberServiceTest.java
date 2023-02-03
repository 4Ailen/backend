package com.aliens.friendship.member.service;

import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @DisplayName("성공: 회원가입")
    void CreateMember_When_GivenValidJoinDto() throws Exception {
        //given: 회원가입 정보
        JoinDto mockJoinDto = createMockJoinDto("test@case.com","TestPassword");

        //when: 회원가입
        memberService.join(mockJoinDto);

        //then: 회원가입 성공
        Member findMember = memberRepository.findByEmail(mockJoinDto.getEmail()).get();
        assertEquals(mockJoinDto.getEmail(), findMember.getEmail());
    }

    @Test
    @Transactional
    @DisplayName("예외: 회원가입시 이미 존재하는 이메일일 경우")
    void ThrowException_When_GivenExistEmail() throws Exception {

        //given: 이미 존재하는 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        JoinDto mockJoinDto2 = createMockJoinDto("test@case.com", "TestPassword");
        memberService.join(mockJoinDto);

        //when: 중복 회원 가입
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.join(mockJoinDto2);
        });

        //then: 예외 발생
        assertEquals("이미 사용중인 이메일입니다.", exception.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("성공: 회원가입시 비밀번호 암호화")
    void EncryptPassword_When_Join() throws Exception {
        //given: 회원가입시 비밀번호 암호화
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        memberService.join(mockJoinDto);

        //when: 멤버 조회
        Member retrievedMember = memberRepository.findByEmail("test@case.com").get();

        //then: 비밀번호 암호화 확인
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        assertTrue(passwordEncoder.matches("TestPassword", retrievedMember.getPassword()));
    }

    private JoinDto createMockJoinDto(String email, String password) {
        JoinDto mockJoinDto = new JoinDto();
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        mockJoinDto.setEmail(email);
        mockJoinDto.setPassword(password);
        mockJoinDto.setName("Ryan");
        mockJoinDto.setMbti("ENFJ");
        mockJoinDto.setGender("MALE");
        mockJoinDto.setNationality(new Nationality(1, "South Korea"));
        mockJoinDto.setAge(20);
        mockJoinDto.setImage(mockMultipartFile);
        return mockJoinDto;
    }
}