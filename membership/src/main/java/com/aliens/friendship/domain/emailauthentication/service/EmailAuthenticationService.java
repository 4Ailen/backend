package com.aliens.friendship.domain.emailauthentication.service;

import com.aliens.db.emailauthentication.entity.EmailAuthenticationEntity;
import com.aliens.db.emailauthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.domain.emailauthentication.exception.EmailInvalidTokenException;
import com.aliens.friendship.domain.emailauthentication.exception.EmailVerificationTimeOutException;
import com.aliens.friendship.domain.member.exception.EmailVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailAuthenticationService {

    private final EmailAuthenticationRepository emailAuthenticationRepository;

    @Value("${spring.mail.domain}")
    private String domainUrl;

    public void checkEmailAuthentication(String email) throws Exception {
        EmailAuthenticationEntity emailAuthenticationEntity = emailAuthenticationRepository.findByEmail(email);
        if (emailAuthenticationEntity.getStatus() == EmailAuthenticationEntity.Status.NOT_VERIFIED) {
            throw new EmailVerificationException();
        }
        emailAuthenticationRepository.deleteByEmail(email);
    }



    public boolean deleteExistingEmailAuthentication(String email) {
        if (emailAuthenticationRepository.existsByEmail(email)) {
            emailAuthenticationRepository.deleteByEmail(email);
        }
        return true;
    }


    public SimpleMailMessage createAuthenticationMail(EmailAuthenticationEntity emailAuthenticationEntity) {
        SimpleMailMessage authenticationEmail = new SimpleMailMessage();
        authenticationEmail.setTo(emailAuthenticationEntity.getEmail());
        authenticationEmail.setSubject("[FriendShip] 회원가입 이메일 인증");
        authenticationEmail.setText("아래에 주어진 링크 접속을 통해 이메일 인증 완료 후 어플로 돌아가 회원가입을 완료해주세요.\n\n" + domainUrl + "api/v1/email/" + emailAuthenticationEntity.getEmail() + "/verification?token=" + emailAuthenticationEntity.getId());
        return authenticationEmail;
    }

    public boolean checkValidToken(String savedToken, String givenToken) throws Exception {
        if (!savedToken.equals(givenToken)) {
            throw new EmailInvalidTokenException();
        }
        return true;
    }

    public boolean checkExpirationTime(EmailAuthenticationEntity emailAuthenticationEntity) throws Exception {
        if (Instant.now().isAfter(emailAuthenticationEntity.getExpirationAt())) {
            throw new EmailVerificationTimeOutException();
        }
        return true;
    }


    public EmailAuthenticationEntity findByEmail(String email) {
        return emailAuthenticationRepository.findByEmail(email);
    }

    public boolean isNullEmailAuthenticationEntity(EmailAuthenticationEntity emailAuthenticationEntity) {
        if (emailAuthenticationEntity == null) {
            return true;
        }
        else{
            return false;
        }
    }


    public String getMemberAuthenticationStatus(String email, boolean isJoinedEmail,EmailAuthenticationEntity emailAuthenticationEntity) {
        boolean isNullEmailAuthenticationEntity = isNullEmailAuthenticationEntity(emailAuthenticationEntity);

        if (isNullEmailAuthenticationEntity && !isJoinedEmail){
            return "JOINED"; // 이메일 인증 후 회원가입된 상태
        }
        else if (isNullEmailAuthenticationEntity && isJoinedEmail){
            return "EMAIL_NOT_SENT"; // 이메일 전송 요청필요 상태
        }
        String status = emailAuthenticationEntity.getStatus().toString();

        if (status.equals("VERIFIED")) {
            if (Instant.now().isAfter(emailAuthenticationEntity.getExpirationAt())) {
                emailAuthenticationRepository.deleteByEmail(email);
                return "REAUTHENTICATION_REQUIRED"; // 이메일 인증이 되었으나, 기간 만료로 재인증이 필요
            } else {
                return "AUTHENTICATED"; // 이메일 인증 완료
            }
        } else {
            return "EMAIL_SENT_NOT_AUTHENTICATED"; // 이메일 인증 미완료
        }
    }

    public void register(EmailAuthenticationEntity emailAuthenticationEntity) {
        emailAuthenticationRepository.save(emailAuthenticationEntity);
    }

    public void changeToVerifyStatus(EmailAuthenticationEntity emailAuthenticationEntity) {
        emailAuthenticationEntity.updateStatus(EmailAuthenticationEntity.Status.VERIFIED);
    }
}