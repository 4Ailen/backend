package com.aliens.friendship.emailAuthentication.service;

import com.aliens.friendship.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailAuthenticationService {

    private final JavaMailSender javaMailSender;
    private final EmailAuthenticationRepository emailAuthenticationRepository;

    private final MemberRepository memberRepository;

    public void sendEmail(String email) throws Exception {
        deleteExistingEmailAuthentication(email);
        checkJoinedEmail(email);
        EmailAuthentication authenticationEmail = EmailAuthentication.createEmailAuthentication(email);
        emailAuthenticationRepository.save(authenticationEmail);
        SimpleMailMessage authenticationMail = createAuthenticationMail(authenticationEmail);
        javaMailSender.send(authenticationMail);
        System.out.println("sendEmail");
    }

    private void deleteExistingEmailAuthentication(String email) {
        if (emailAuthenticationRepository.existsByEmail(email)) {
            emailAuthenticationRepository.deleteByEmail(email);
        }
    }

    private void checkJoinedEmail(String email) throws Exception {
        if (memberRepository.existsByEmail(email)) {
            throw new Exception("이미 회원가입된 이메일입니다.");
        }
    }

    private SimpleMailMessage createAuthenticationMail(EmailAuthentication emailAuthentication) {
        SimpleMailMessage authenticationEmail = new SimpleMailMessage();
        authenticationEmail.setTo(emailAuthentication.getEmail());
        authenticationEmail.setSubject("회원가입 이메일 인증");
        authenticationEmail.setText("http://localhost:8080/email/" + emailAuthentication.getEmail() + "/verification?token=" + emailAuthentication.getId());
        return authenticationEmail;
    }
}
