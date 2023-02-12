package com.aliens.friendship.member.service;

import com.aliens.friendship.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.controller.dto.WithdrawalDto;
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


@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void CreateMember_Success_When_GivenValidJoinDto() throws Exception {
        //given: 회원가입 정보
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");

        //when: 회원가입
        memberService.join(mockJoinDto);

        //then: 회원가입 성공
        Member findMember = memberRepository.findByEmail(mockJoinDto.getEmail()).get();
        assertEquals(mockJoinDto.getEmail(), findMember.getEmail());
    }

    @Test
    @DisplayName("회원가입 예외: 이미 존재하는 이메일일 경우")
    void CreateMember_ThrowException_When_GivenExistEmail() throws Exception {

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
    @DisplayName("비밀번호 암호화 성공")
    void EncryptPassword_Success_When_Join() throws Exception {
        //given: 회원가입시 비밀번호 암호화
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        memberService.join(mockJoinDto);

        //when: 멤버 조회
        Member retrievedMember = memberRepository.findByEmail("test@case.com").get();

        //then: 비밀번호 암호화 확인
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        assertTrue(passwordEncoder.matches("TestPassword", retrievedMember.getPassword()));
    }

    //TODO: 회원탈퇴 성공, 회원탈퇴 예외: 존재하지 않는 회원일 경우, 회원탈퇴 예외: JWT 토큰이 유효하지 않은 경우, 회원탈퇴 예외: 비밀번호 불일치
    @Test
    @DisplayName("회원탈퇴 성공")
    void DeleteMember_Success_When_GivenValidMemberAndAuthority() throws Exception {
        //given: 유효한 회원 탈퇴 정보
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        memberService.join(mockJoinDto);
        WithdrawalDto mockWithdrawalDto = WithdrawalDto.builder().email("test@case.com").password("TestPassword").build();

        //when: 회원탈퇴
        memberService.withdrawal(mockWithdrawalDto);

        //then: 회원탈퇴 성공
        assertFalse(memberRepository.findByEmail("test@case.com").isPresent());
    }

    @Test
    @DisplayName("회원탈퇴 예외: 존재하지 않는 회원일 경우")
    void DeleteMember_ThrowException_When_GivenNotExistMember() throws Exception {
        //given: 회원가입 되어있지 않은 회원의 회원탈퇴 정보
        WithdrawalDto mockWithdrawalDto = WithdrawalDto.builder().email("test@case.com").password("TestPassword").build();

        //when: 회원탈퇴
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.withdrawal(mockWithdrawalDto);
        });

        //then: 예외 발생
        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원탈퇴 예외: 비밀번호 불일치")
    void DeleteMember_ThrowException_When_GivenNotMatchPassword() throws Exception {
        //given: 비밀번호 불일치 회원탈퇴 정보
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        memberService.join(mockJoinDto);
        WithdrawalDto mockWithdrawalDto = WithdrawalDto.builder().email("test@case.com").password("WrongPassword").build();

        //when: 회원탈퇴
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.withdrawal(mockWithdrawalDto);
        });

        //then: 예외 발생
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

//    @Test
//    @Transactional
//    @DisplayName("회원탈퇴 예외: JWT 토큰이 유효하지 않은 경우")
//    void DeleteMember_Success_When_GivenValidMemberAndAuthority() throws Exception {
//        //given: 유효한 회원 탈퇴 정보
//        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
//        memberService.join(mockJoinDto);
//        WithdrawalDto mockWithdrawalDto = WithdrawalDto.builder().email("test@case.com").password("TestPassword").build();
//
//        //when: 회원탈퇴
//        Exception exception = assertThrows(Exception.class, () -> {
//            memberService.withdrawal(mockWithdrawalDto);
//        });
//
//        //then: 예외 발생
//        assertEquals("JWT 토큰이 유효하지 않습니다.", exception.getMessage());
//    }

//    TODO: 회원 정보 요청 성공, 회원 정보 요청 예외: 존재하지 않는 회원일 경우, 회원 정보 요청 예외: JWT 토큰이 유효하지 않은 경우
    @Test
    @DisplayName("회원 정보 요청 성공")
    void GetMemberInfo_Success_When_GivenValidMember() throws Exception {
        //given: 회원가입된 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        memberService.join(mockJoinDto);
        int memberId = memberRepository.findByEmail("test@case.com").get().getId();
        MemberInfoDto memberDto = memberService.getMemberInfo(memberId);

        //then: 회원 정보 요청 성공
        assertEquals("test@case.com", memberDto.getEmail());
        assertEquals("1998-12-31", memberDto.getBirthday());
        assertEquals(24, memberDto.getAge());
    }

    @Test
    @DisplayName("회원 정보 요청 예외: 존재하지 않는 회원일 경우")
    void GetMemberInfo_ThrowException_When_GivenNotExistMember() throws Exception {
        //when: 회원 정보 요청 (가입되어있지 않은 회원)
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.getMemberInfo(9999);
        });

        //then: 예외 발생
        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
    }






    private JoinDto createMockJoinDto(String email, String password) {
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        return JoinDto.builder()
                .email(email)
                .password(password)
                .name("Ryan")
                .mbti("ENFJ")
                .gender("MALE")
                .nationality(new Nationality(1, "South Korea"))
                .birthday("1998-12-31")
                .image(mockMultipartFile)
                .build();
    }
}