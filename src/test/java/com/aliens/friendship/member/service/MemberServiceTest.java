package com.aliens.friendship.member.service;

import com.aliens.friendship.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.global.config.security.CustomUserDetails;
import com.aliens.friendship.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Transactional
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    EmailAuthenticationRepository emailAuthenticationRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    ProfileImageService profileImageService;

    @Mock
    JavaMailSender javaMailSender;

    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void CreateMember_Success() throws Exception {
        //given: 회원가입 정보
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        EmailAuthentication mockEmailAuthentication = EmailAuthentication.createEmailAuthentication(mockJoinDto.getEmail());
        mockEmailAuthentication.updateStatus(EmailAuthentication.Status.VERIFIED);
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.empty());
        when(emailAuthenticationRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(mockEmailAuthentication);
        when(profileImageService.uploadProfileImage(mockJoinDto.getImage())).thenReturn("/testUrl");

        //when: 회원가입
        memberService.join(mockJoinDto);

        //then: 회원가입 성공
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(emailAuthenticationRepository, times(1)).findByEmail(anyString());
        verify(profileImageService, times(1)).uploadProfileImage(any(MultipartFile.class));
    }

    @Test
    @DisplayName("회원가입 예외: 이미 존재하는 이메일일 경우")
    void CreateMember_ThrowException_When_GivenExistEmail() throws Exception {
        //given: 이미 존재하는 이메일
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member mockMember = createSpyMember(mockJoinDto);
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.of(mockMember));

        //when: 중복 회원 가입
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.join(mockJoinDto);
        });

        //then: 예외 발생
        verify(memberRepository, times(0)).save(any(Member.class));
        assertEquals("이미 사용중인 이메일입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원가입 예외: 이메일 인증이 완료되지 않은 이메일일 경우")
    void CreateMember_ThrowException_When_GivenUnauthenticatedEmail() throws Exception {
        //given: 이메일 인증이 완료되지 않은 이메일
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        EmailAuthentication mockEmailAuthentication = EmailAuthentication.createEmailAuthentication(mockJoinDto.getEmail());
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.empty());
        when(emailAuthenticationRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(mockEmailAuthentication);

        //when: 회원가입
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.join(mockJoinDto);
        });

        //then: 예외 발생
        verify(memberRepository, times(0)).save(any(Member.class));
        assertEquals("이메일 인증이 완료되지 않았습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void DeleteMember_Success() throws Exception {
        //given: 가입 및 로그인 된 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member mockMember = createSpyMember(mockJoinDto);
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.of(mockMember));

        UserDetails userDetails = CustomUserDetails.of(mockMember);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when: 회원탈퇴
        memberService.withdraw("TestPassword");

        //then: 회원탈퇴 성공
        verify(memberRepository, times(1)).delete(any(Member.class));
    }

    @Test
    @DisplayName("회원탈퇴 예외: 비밀번호 불일치")
    void DeleteMember_ThrowException_When_GivenNotMatchPassword() throws Exception {
        //given: 가입 및 로그인 된 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member mockMember = createSpyMember(mockJoinDto);
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.of(mockMember));

        UserDetails userDetails = CustomUserDetails.of(mockMember);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when: 회원탈퇴
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.withdraw("NotMatchPassword");
        });

        //then: 회원탈퇴 성공
        verify(memberRepository, times(0)).delete(any(Member.class));
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 정보 요청 성공")
    void GetMemberInfo_Success() throws Exception {
        //given: 회원가입된 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        when(spyMember.getId()).thenReturn(1);
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.of(spyMember));

        UserDetails userDetails = CustomUserDetails.of(spyMember);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when: 회원 정보 요청
        MemberInfoDto memberDto = memberService.getMemberInfo();

        //then: 회원 정보 요청 성공
        verify(memberRepository, times(1)).findByEmail(anyString());
        assertEquals("test@case.com", memberDto.getEmail());
        assertEquals("1998-12-31", memberDto.getBirthday());
        assertEquals(24, memberDto.getAge());
    }

    @Test
    @DisplayName("회원 임시 비밀번호 발급 요청 성공")
    void IssueTemporaryPassword_Success() throws Exception {
        // given
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        when(memberRepository.findByEmail(spyMember.getEmail())).thenReturn(Optional.of(spyMember));
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // when
        memberService.issueTemporaryPassword(spyMember.getEmail(), spyMember.getName());

        // then
        verify(memberRepository, times(1)).findByEmail(anyString());
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
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

    private Member createSpyMember(JoinDto joinDto) {
        joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        Member member = Member.ofUser(joinDto);
        Member spyMember = spy(member);
        return spyMember;
    }
}