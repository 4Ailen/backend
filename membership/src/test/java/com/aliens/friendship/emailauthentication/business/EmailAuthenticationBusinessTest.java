package com.aliens.friendship.emailauthentication.business;


import com.aliens.db.emailauthentication.entity.EmailAuthenticationEntity;
import com.aliens.friendship.domain.emailauthentication.business.EmailAuthenticationBusiness;
import com.aliens.friendship.domain.emailauthentication.converter.EmailAuthenticationConverter;
import com.aliens.friendship.domain.emailauthentication.service.EmailAuthenticationService;
import com.aliens.friendship.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EmailAuthenticationBusinessTest {

    @Mock
    private EmailAuthenticationService emailAuthenticationService;

    @Mock
    private MemberService memberService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailAuthenticationConverter emailAuthenticationConverter;

    @InjectMocks
    private EmailAuthenticationBusiness emailAuthenticationBusiness;

    private EmailAuthenticationEntity emailAuthenticationEntity;
    private String email = "test@example.com";
    private String token = "test-token";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        emailAuthenticationEntity = new EmailAuthenticationEntity();
    }

    @Test
    @DisplayName("이메일 인증상태 조회 - 성공")
    public void testGetEmailAuthenticationStatus() {
        // Set up mock behaviors
        when(emailAuthenticationService.findByEmail(anyString())).thenReturn(emailAuthenticationEntity);
        when(memberService.isJoinedEmail(email)).thenReturn(false);
        when(emailAuthenticationService.getMemberAuthenticationStatus(anyString(), anyBoolean(), any(EmailAuthenticationEntity.class)))
                .thenReturn("verified");

        // Call the method to be tested
        String status = emailAuthenticationBusiness.getEmailAuthenticationStatus(email);

        // Verify the result
        assertEquals("verified", status);
    }

    @Test
    @DisplayName("인증요청 이메일 전송 - 성공")
    public void testSendAuthenticationEmail() throws Exception {
        // Set up mock behaviors
        when(emailAuthenticationService.deleteExistingEmailAuthentication(email)).thenReturn(true);
        when(memberService.checkJoinedEmail(email)).thenReturn(false);
        when(emailAuthenticationConverter.toAuthenticationEntity(email)).thenReturn(emailAuthenticationEntity);
        when(emailAuthenticationService.createAuthenticationMail(emailAuthenticationEntity))
                .thenReturn(new SimpleMailMessage());

        // Call the method to be tested
        emailAuthenticationBusiness.sendAuthenticationEmail(email);

        // Verify the interactions
        verify(emailAuthenticationService, times(1)).deleteExistingEmailAuthentication(email);
        verify(emailAuthenticationService, times(1)).register(emailAuthenticationEntity);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("이메일 인증 확인 - 성공")
    public void testValidateEmail() throws Exception {
        // Set up mock behaviors
        when(emailAuthenticationService.findByEmail(email)).thenReturn(emailAuthenticationEntity);
        when(emailAuthenticationService.checkValidToken(emailAuthenticationEntity.getId(), token)).thenReturn(true);
        when(emailAuthenticationService.checkExpirationTime(emailAuthenticationEntity)).thenReturn(false);

        // Call the method to be tested
        emailAuthenticationBusiness.validateEmail(email, token);

        // Verify the interactions
        verify(emailAuthenticationService, times(1)).changeToVerifyStatus(emailAuthenticationEntity);
        verify(emailAuthenticationService, times(1)).register(emailAuthenticationEntity);
    }
}