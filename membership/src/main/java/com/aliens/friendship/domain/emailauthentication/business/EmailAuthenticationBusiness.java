package com.aliens.friendship.domain.emailauthentication.business;

import com.aliens.db.emailauthentication.entity.EmailAuthenticationEntity;
import com.aliens.friendship.domain.emailauthentication.converter.EmailAuthenticationConverter;
import com.aliens.friendship.domain.emailauthentication.service.EmailAuthenticationService;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.common.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RequiredArgsConstructor
@Business
@Slf4j
public class EmailAuthenticationBusiness {

    private final EmailAuthenticationService emailAuthenticationService;
    private final MemberService memberService;
    private final JavaMailSender javaMailSender;
    private final EmailAuthenticationConverter emailAuthenticationConverter;

    /**
     * 이메일 인증상태 조회
     */
    public String getEmailAuthenticationStatus(String email) {
        //이메일 인증조회
        EmailAuthenticationEntity emailAuthenticationEntity = emailAuthenticationService.findByEmail(email);

        //이메일 회원 존재유무 체크
        boolean isJoinedEmail = memberService.isJoinedEmail(email);

        //이메일에 대한 사용자 인증상태 추출
        String status = emailAuthenticationService.getMemberAuthenticationStatus(email,isJoinedEmail,emailAuthenticationEntity);

        return status;
    }

    /**
     * 인증요청 이메일 전송
     */
    public void sendAuthenticationEmail(String email) throws Exception {
        //이메일 인증 과거 삭제
        emailAuthenticationService.deleteExistingEmailAuthentication(email);

        // 이미 회원가입된 이메일 체크
        memberService.checkJoinedEmail(email);

        //email -> emailAuthenticationEntity
        EmailAuthenticationEntity emailAuthenticationEntity  = emailAuthenticationConverter.toAuthenticationEntity(email);

        //엔티티 저장
        emailAuthenticationService.register(emailAuthenticationEntity);

        //메일 생성
        SimpleMailMessage authenticationMail = emailAuthenticationService.createAuthenticationMail(emailAuthenticationEntity);

        //메일 전송
        javaMailSender.send(authenticationMail);
    }

    public void validateEmail(String email, String token) throws Exception {
        // email -> entity
        EmailAuthenticationEntity emailAuthenticationEntity = emailAuthenticationService.findByEmail(email);

        //토큰 체크
        emailAuthenticationService.checkValidToken(emailAuthenticationEntity.getId(),token);

        //인증시간 만료체크
        emailAuthenticationService.checkExpirationTime(emailAuthenticationEntity);

        //엔티티상태를 인증완료로 변경
        emailAuthenticationService.changeToVerifyStatus(emailAuthenticationEntity);

        //엔티티 저장
        emailAuthenticationService.register(emailAuthenticationEntity);
    }
}
