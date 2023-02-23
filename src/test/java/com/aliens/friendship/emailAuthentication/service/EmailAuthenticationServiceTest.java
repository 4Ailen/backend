package com.aliens.friendship.emailAuthentication.service;

import com.aliens.friendship.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.transaction.Transactional;

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


    @Test
    @DisplayName("이메일 전송 성공")
    void SendEmail_Success_When_GivenValidEmail() throws Exception {
        // given: 인증할 이메일
        String email = "test@case.com";

        // when: 이메일 전송 함수 실행
        emailAuthenticationService.sendEmail(email);

        // then: 이메일 인증 객체 생성과 전송 확인
        verify(emailAuthenticationRepository, times(1)).save(any(EmailAuthentication.class));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("이메일 전송 예외: 이미 가입된 이메일인 경우")
    void SendEmail_Exception_When_GivenJoinedEmail() throws Exception {
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
}