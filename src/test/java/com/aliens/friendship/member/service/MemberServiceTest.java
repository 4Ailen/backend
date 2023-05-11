package com.aliens.friendship.member.service;

import com.aliens.friendship.domain.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.domain.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.domain.member.service.ProfileImageService;
import com.aliens.friendship.global.config.security.CustomUserDetails;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.domain.Nationality;
import com.aliens.friendship.domain.member.exception.EmailVerificationException;
import com.aliens.friendship.domain.member.exception.PasswordChangeFailedException;
import com.aliens.friendship.domain.member.repository.MemberRepository;
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

import static com.aliens.friendship.domain.member.exception.MemberExceptionCode.EMAIL_VERIFICATION_NOT_COMPLETED;
import static com.aliens.friendship.domain.member.exception.MemberExceptionCode.PASSWORD_CHANGE_FAILED_EXCEPTION;
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
        when(profileImageService.uploadProfileImage(mockJoinDto.getProfileImage())).thenReturn("/testUrl");

        //when: 회원가입
        memberService.join(mockJoinDto);

        //then: 회원가입 성공
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(emailAuthenticationRepository, times(1)).findByEmail(anyString());
        verify(profileImageService, times(1)).uploadProfileImage(any(MultipartFile.class));
    }

    @Test
    @DisplayName("회원가입 성공: 프로필 이미지가 없는 경우")
    void CreateMember_Success_When_ProfileImageIsNull() throws Exception {
        //given: 프로필 이미지가 없는 회원가입 정보
        JoinDto mockJoinDto = JoinDto.builder()
                        .email("test@case.com")
                        .password("TestPassword")
                        .name("Ryan")
                        .mbti("ENFJ")
                        .gender("MALE")
                        .nationality(new Nationality(1, "South Korea"))
                        .birthday("1998-12-31")
                        .profileImage(null)
                        .build();
        String DEFAULT_PROFILE_IMAGE_PATH = "/files/default_profile_image.png";
        EmailAuthentication mockEmailAuthentication = EmailAuthentication.createEmailAuthentication(mockJoinDto.getEmail());
        mockEmailAuthentication.updateStatus(EmailAuthentication.Status.VERIFIED);
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.empty());
        when(emailAuthenticationRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(mockEmailAuthentication);
        when(profileImageService.uploadProfileImage(mockJoinDto.getProfileImage())).thenReturn(DEFAULT_PROFILE_IMAGE_PATH);

        //when: 회원가입
        memberService.join(mockJoinDto);

        //then: 회원가입 성공
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(emailAuthenticationRepository, times(1)).findByEmail(anyString());
        verify(profileImageService, times(1)).uploadProfileImage(null);
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
        EmailVerificationException exception = assertThrows(EmailVerificationException.class, () -> {
            memberService.join(mockJoinDto);
        });

        //then: 예외 발생
        verify(memberRepository, times(0)).save(any(Member.class));
        assertEquals(EMAIL_VERIFICATION_NOT_COMPLETED.getMessage(), exception.getExceptionCode().getMessage());
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

        //then: 예외 발생
        verify(memberRepository, times(0)).delete(any(Member.class));
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 정보 요청 성공")
    void GetMemberInfo_Success() throws Exception {
        //given: 회원가입된 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
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
        // given: 가입 및 로그인 된 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        when(memberRepository.findByEmailAndName(spyMember.getEmail(), spyMember.getName())).thenReturn(Optional.of(spyMember));
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // when: 임시 비밀번호 발급
        memberService.issueTemporaryPassword(spyMember.getEmail(), spyMember.getName());

        // then: 임시 비밀번호 발급 요청 성공
        verify(memberRepository, times(1)).findByEmailAndName(anyString(), anyString());
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("회원 임시 비밀번호 발급 예외: 가입되지 않은 이메일인 경우")
    void IssueTemporaryPassword_ThrowException_When_GivenNotJoinedEmail() throws Exception {
        // given: 회원가입 되지 않은 이메일
        String email = "test@case.com", name = "test";

        // when: 임시 비밀번호 발급
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.issueTemporaryPassword(email, name);
        });

        // then: 예외 발생
        verify(javaMailSender, times(0)).send(any(SimpleMailMessage.class));
        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 임시 비밀번호 발급 예외: 이름이 틀린 경우")
    void IssueTemporaryPassword_ThrowException_When_GivenNotMatchName() throws Exception {
        // given: 회원가입 된 정보와 다른 이름
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        String email = spyMember.getEmail(), invalidName = spyMember.getName() + "invalid";

        // when: 임시 비밀번호 발급
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.issueTemporaryPassword(email, invalidName);
        });

        // then: 예외 발생
        verify(javaMailSender, times(0)).send(any(SimpleMailMessage.class));
        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void ChangePassword_Success() throws Exception {
        // given: 가입 및 로그인 된 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        PasswordUpdateRequestDto passwordUpdateRequestDto = PasswordUpdateRequestDto.builder()
                .currentPassword("currentPassword")
                .newPassword("newPassword").build();
        when(memberRepository.findByEmail(spyMember.getEmail())).thenReturn(Optional.of(spyMember));
        when(passwordEncoder.matches(passwordUpdateRequestDto.getCurrentPassword(), spyMember.getPassword())).thenReturn(true);

        UserDetails userDetails = CustomUserDetails.of(spyMember);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when: 비밀번호 변경
        memberService.changePassword(passwordUpdateRequestDto);

        // then: 비밀번호 변경 성공
        verify(memberRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("비밀번호 변경 예외: 현재 비밀번호가 불일치하는 경우")
    void ChangePassword_ThrowException_When_GivenNotMatchCurrentPassword() throws Exception {
        //given: 가입 및 로그인 된 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        PasswordUpdateRequestDto passwordUpdateRequestDto = PasswordUpdateRequestDto.builder()
                .currentPassword(mockJoinDto.getPassword())
                .newPassword("TestNewPassword").build();
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.of(spyMember));
        when(passwordEncoder.matches(passwordUpdateRequestDto.getCurrentPassword(), spyMember.getPassword())).thenReturn(false);

        UserDetails userDetails = CustomUserDetails.of(spyMember);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when: 비밀번호 변경
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.changePassword(passwordUpdateRequestDto);
        });

        //then: 예외 발생
        verify(memberRepository, times(0)).save(any(Member.class));
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 예외: 새 비밀번호가 현재 비밀번호와 일치하는 경우")
    void ChangePassword_ThrowException_When_GivenNewPasswordMatchCurrentPassword() throws Exception {
        //given: 가입 및 로그인 된 회원
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        PasswordUpdateRequestDto passwordUpdateRequestDto = PasswordUpdateRequestDto.builder()
                .currentPassword(mockJoinDto.getPassword())
                .newPassword(mockJoinDto.getPassword()).build();
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.of(spyMember));
        when(passwordEncoder.matches(passwordUpdateRequestDto.getCurrentPassword(), spyMember.getPassword())).thenReturn(true);

        UserDetails userDetails = CustomUserDetails.of(spyMember);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when: 비밀번호 변경
        PasswordChangeFailedException exception = assertThrows(PasswordChangeFailedException.class, () -> {
            memberService.changePassword(passwordUpdateRequestDto);
        });

        //then: 예외 발생
        verify(memberRepository, times(0)).save(any(Member.class));
        assertEquals(PASSWORD_CHANGE_FAILED_EXCEPTION.getMessage(), exception.getExceptionCode().getMessage());
    }

    @Test
    @DisplayName("프로필 이름과 mbti 값 변경 성공")
    void ChangeProfileNameAndMbti_Success() throws Exception {
        // given
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        String newName = "test";
        String newMbti = "ISFJ";
        when(memberRepository.findByEmail(spyMember.getEmail())).thenReturn(Optional.of(spyMember));

        UserDetails userDetails = CustomUserDetails.of(spyMember);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        memberService.changeProfileNameAndMbti(newName, newMbti);

        // then
        verify(memberRepository, times(1)).findByEmail(anyString());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("프로필 이미지 수정 성공")
    void ChangeProfileImage_Success() throws Exception {
        // given
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        MockMultipartFile newProfileImage = new MockMultipartFile("profileImage", "test2.jpg", "image/jpeg", "test data".getBytes());
        when(memberRepository.findByEmail(spyMember.getEmail())).thenReturn(Optional.of(spyMember));
        when(profileImageService.deleteProfileImage(spyMember.getProfileImageUrl())).thenReturn(true);
        when(profileImageService.uploadProfileImage(newProfileImage)).thenReturn("/testUrl");

        UserDetails userDetails = CustomUserDetails.of(spyMember);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        memberService.changeProfileImage(newProfileImage);

        // then
        verify(memberRepository, times(1)).findByEmail(anyString());
        verify(profileImageService, times(1)).deleteProfileImage(anyString());
        verify(profileImageService, times(1)).uploadProfileImage(any(MultipartFile.class));
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("인증 상태 반환 성공")
    void GetMeberAuthenticationStatus_Success() throws Exception {
        // given
        String email = "test@case.com";
        EmailAuthentication emailAuthentication = EmailAuthentication.createEmailAuthentication(email);
        when(emailAuthenticationRepository.findByEmail(email)).thenReturn(emailAuthentication);

        // when
        String status = memberService.getMemberAuthenticationStatus(email);

        // then
        verify(emailAuthenticationRepository, times(1)).findByEmail(anyString());
        assertEquals(status, "NOT_AUTHENTICATED");
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
                .profileImage(mockMultipartFile)
                .build();
    }

    private Member createSpyMember(JoinDto joinDto) {
        joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        Member member = Member.ofUser(joinDto);
        Member spyMember = spy(member);
        return spyMember;
    }
}