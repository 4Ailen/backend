package com.aliens.friendship.domain.emailAuthentication.service;

import com.aliens.friendship.domain.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.domain.emailAuthentication.exception.EmailAlreadyRegisteredException;
import com.aliens.friendship.domain.emailAuthentication.exception.EmailInvalidTokenException;
import com.aliens.friendship.domain.emailAuthentication.exception.EmailVerificationTimeOutException;
import com.aliens.friendship.domain.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.aliens.friendship.domain.emailAuthentication.exception.EmailAuthenticationExceptionCode.EMAIL_INVALID_TOKEN;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailAuthenticationService {

    private final JavaMailSender javaMailSender;
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final MemberRepository memberRepository;
    @Value("${spring.domain}")
    private String domainUrl;

    public void sendEmail(String email) throws Exception {
        deleteExistingEmailAuthentication(email);
        checkJoinedEmail(email);
        EmailAuthentication authenticationEmail = EmailAuthentication.createEmailAuthentication(email);
        emailAuthenticationRepository.save(authenticationEmail);
        SimpleMailMessage authenticationMail = createAuthenticationMail(authenticationEmail);
        javaMailSender.send(authenticationMail);
    }

    public void validateEmail(String email, String token) throws Exception {
        EmailAuthentication emailAuthentication = emailAuthenticationRepository.findByEmail(email);
        checkValidToken(emailAuthentication.getId(), token);
        checkExpirationTime(emailAuthentication);
        emailAuthentication.updateStatus(EmailAuthentication.Status.VERIFIED);
        emailAuthenticationRepository.save(emailAuthentication);
    }

    private void deleteExistingEmailAuthentication(String email) {
        if (emailAuthenticationRepository.existsByEmail(email)) {
            emailAuthenticationRepository.deleteByEmail(email);
        }
    }

    private void checkJoinedEmail(String email) throws Exception {
        if (memberRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }
    }

    private SimpleMailMessage createAuthenticationMail(EmailAuthentication emailAuthentication) {
        SimpleMailMessage authenticationEmail = new SimpleMailMessage();
        authenticationEmail.setTo(emailAuthentication.getEmail());
        authenticationEmail.setSubject("[FriendShip] 회원가입 이메일 인증");
        authenticationEmail.setText("아래에 주어진 링크 접속을 통해 이메일 인증 완료 후 어플로 돌아가 회원가입을 완료해주세요.\n\n" + domainUrl + "api/v1/email/" + emailAuthentication.getEmail() + "/verification?token=" + emailAuthentication.getId());
        return authenticationEmail;
    }

    private void checkValidToken(String savedToken, String givenToken) throws Exception {
        if (!savedToken.equals(givenToken)) {
            throw new EmailInvalidTokenException();
        }
    }

    private void checkExpirationTime(EmailAuthentication emailAuthentication) throws Exception {
        if (Instant.now().isAfter(emailAuthentication.getExpirationTime())) {
            throw new EmailVerificationTimeOutException();
        }
    }
}