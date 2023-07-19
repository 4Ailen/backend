package com.aliens.friendship.emailAuthentication.service;

import com.aliens.friendship.domain.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.domain.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.domain.emailAuthentication.service.EmailAuthenticationService;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class EmailAuthenticationServiceTest {

    @InjectMocks
    EmailAuthenticationService emailAuthenticationService;

    @Mock
    EmailAuthenticationRepository emailAuthenticationRepository;

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("이메일 전송 성공")
    void SendEmail_Success_When_GivenValidEmail() throws Exception {
        // given: 인증할 이메일
        String email = "test@case.com";
        when(memberRepository.existsByEmail(email)).thenReturn(false);

        // when: 이메일 전송 함수 실행
        emailAuthenticationService.sendEmail(email);

        // then: 이메일 인증 객체 생성과 전송 확인
        verify(emailAuthenticationRepository, times(1)).save(any(EmailAuthentication.class));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("이메일 전송 예외: 이미 가입된 이메일인 경우")
    void SendEmail_ThrowException_When_GivenJoinedEmail() throws Exception {
        // given: 회원가입
        String email = "test@case.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);

        // when: 이메일 전송 함수 실행
        Exception exception = assertThrows(Exception.class, () -> {
            emailAuthenticationService.sendEmail(email);
        });

        // then: 예외 발생
        verify(javaMailSender, times(0)).send(any(SimpleMailMessage.class));
        assertEquals("이미 회원가입된 이메일입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void ValidateEmail_Success_When_GivenValidEmailAndToken() throws Exception {
        // given: 이메일 인증 객체 생성
        String email = "test@case.com";
        EmailAuthentication emailAuthentication = EmailAuthentication.createEmailAuthentication(email);
        when(emailAuthenticationRepository.findByEmail(email)).thenReturn(emailAuthentication);

        // when: 이메일 인증 함수 실행
        emailAuthenticationService.validateEmail(email, emailAuthentication.getId());

        // then: 이메일 인증 객체 업데이트 확인
        verify(emailAuthenticationRepository, times(1)).save(any(EmailAuthentication.class));
    }

    @Test
    @DisplayName("이메일 인증 예외: 인증 시간이 초과된 경우")
    void ValidateEmail_ThrowException_When_GivenAuthenticationTimeExpired() {
        // given: 이메일 인증 객체 생성(생성시간과 만료시간을 현재시간 이전으로 설정)
        String email = "test@case.com";
        EmailAuthentication mockEmailAuthentication = EmailAuthentication.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .createdTime(Instant.now().minus(10, ChronoUnit.MINUTES))
                .expirationTime(Instant.now().minus(5, ChronoUnit.MINUTES))
                .build();
        when(emailAuthenticationRepository.findByEmail(email)).thenReturn(mockEmailAuthentication);

        // when: 이메일 인증 함수 실행
        Exception exception = assertThrows(Exception.class, () -> {
            emailAuthenticationService.validateEmail(email, mockEmailAuthentication.getId());
        });

        // then: 예외 발생
        assertEquals("이메일 인증 시간이 초과되었습니다.", exception.getMessage());
    }

    @Disabled
    @Test
    @DisplayName("이메일 인증 예외: 유효하지 않은 토큰일 경우")
    void ValidateEmail_ThrowException_When_GivenInvalidToken() {
        // given: 이메일 인증 객체 생성
        String email = "test@case.com";
        EmailAuthentication mockEmailAuthentication = EmailAuthentication.createEmailAuthentication(email);
        StringBuffer validToken = new StringBuffer(mockEmailAuthentication.getId());
        String invalidToken = validToken.reverse().toString();
        when(emailAuthenticationRepository.findByEmail(email)).thenReturn(mockEmailAuthentication);

        // when: 이메일 인증 함수 실행
        Exception exception = assertThrows(Exception.class, () -> {
            emailAuthenticationService.validateEmail(email, invalidToken);
        });

        // then: 예외 발생
        assertEquals("유효하지 않은 이메일 인증 토큰입니다.", exception.getMessage());
    }
}