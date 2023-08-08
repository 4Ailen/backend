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
        authenticationEmail.setSubject("[FriendShip] Account Verification 회원가입 이메일 인증");
        String content = getEmailAuthenticationContent(emailAuthenticationEntity.getEmail(),emailAuthenticationEntity.getId());
        authenticationEmail.setText(content);
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

        if (isNullEmailAuthenticationEntity && isJoinedEmail){
            return "JOINED"; // 이메일 인증 후 회원가입된 상태
        }
        else if (isNullEmailAuthenticationEntity && !isJoinedEmail){
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

    public String getEmailAuthenticationContent(String email, String id) {
        String content = "Thank you for registering with FreindShip App.\n " +
                "To ensure the security of your account, we need to verify your email address.\n\n " +
                "Please click the link below to verify your email and activate your account:\n"+
                domainUrl + "api/v1/email/" + email + "/verification?token=" + id
                +"\n\nIf you are unable to click the link, \n " +
                "you can copy and paste the following URL into your browser\n\n " +
                "If you did not sign up for an account with FreindShip App,please ignore this email.\n " +
                "Your information will not be used for any unauthorized purposes.\n\n " +
                "Thank you for choosing FreindShip App. \n " +
                "If you have any questions or concerns, please contact our support team at this email.\n\n " +
                "Best regards,\n " +
                "The 4Aliens Team\n\n\n\n\n" +
                "안녕하세요!\n\n " +
                "FreindShip App에 가입해 주셔서 감사합니다. \n " +
                "서비스를 사용하시기 위해서는 계정의 보안을 위해 이메일 주소를 인증해야 합니다.\n\n"+
                "아래 링크를 클릭하여 이메일 주소를 인증하고 계정을 활성화해 주세요:\n" +
                domainUrl + "api/v1/email/" + email + "/verification?token=" + id +
                "\n\n링크를 클릭할 수 없는 경우 다음 URL을 브라우저에 복사하여 붙여넣으셔도 됩니다.\n\n " +
                "만약 FreindShip App에 계정을 만든 적이 없는 경우, \n " +
                "이 이메일을 무시해 주세요. 귀하의 정보는 무단으로 사용되지 않습니다.\n\n " +
                "FreindShip App을 선택해 주셔서 감사합니다.\n " +
                "궁금한 점이나 문의사항이 있으시면 해당 이메일로 문의해 주세요.\n\n " +
                "감사합니다,\n " +
                "4Aliens 팀 올림\n";
        return content;
    }
}