package com.aliens.friendship.domain.member.service;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.emailauthentication.exception.EmailAlreadyRegisteredException;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.domain.member.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageService profileImageService;
    private final JavaMailSender javaMailSender;

    @Value("${file-server.domain}")
    private String domainUrl;

    @Transactional
    public boolean changeProfileImage(MemberEntity memberEntity, MultipartFile profileImage) throws Exception {
        memberEntity.updateImageUrl(profileImageService.uploadProfileImage(profileImage));
        memberRepository.save(memberEntity);
        return true;
    }

    @Transactional
    public Long register(MemberEntity memberEntity) {
        memberEntity.updatePassword(passwordEncoder.encode(memberEntity.getPassword()));
        MemberEntity saved = memberRepository.save(memberEntity);
        return saved.getId();
    }

    @Transactional
    public void unregister(MemberEntity memberEntity,String password) throws Exception {
        if (!passwordEncoder.matches(password, memberEntity.getPassword())) {
            throw new InvalidMemberPasswordException();
        }
        memberEntity.updateStatus(MemberEntity.Status.WITHDRAWN);
        memberEntity.updateWithdrawalDate(Instant.now());
    }

    @Transactional
    public void deleteMemberInfoByAdmin(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        memberRepository.deleteById(memberId);
    }

    @Transactional
    public void changePassword(MemberEntity memberEntity, String password) throws Exception {
        memberEntity.updatePassword(passwordEncoder.encode(password));
        memberRepository.save(memberEntity);
    }

    @Transactional
    public void changeSelfIntroduction(MemberEntity memberEntity, String selfIntroductionChangeRequest) throws Exception {
        memberEntity.updateSelfIntroduction(selfIntroductionChangeRequest);
        memberRepository.save(memberEntity);
    }

    @Transactional
    public void changeMbti(MemberEntity memberEntity, MemberEntity.Mbti mbti) {
        memberEntity.updateMbti(mbti);
        memberRepository.save(memberEntity);
    }

    public MemberEntity findByEmailAndName(String email, String name) throws Exception {
        return memberRepository.findByEmailAndName(email,name).orElseThrow(MemberNotFoundException::new);
    }

    public boolean isJoinedEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    public boolean checkDuplicatedAndWithdrawnInAWeekEmail(String email) throws Exception {
        if (memberRepository.findByEmail(email).isPresent()) {
            if (memberRepository.findByEmail(email).get().getStatus() == MemberEntity.Status.WITHDRAWN) {
                throw new WithdrawnMemberWithinAWeekException();
            } else {
                throw new DuplicateMemberEmailException();
            }
        }
        return true;
    }

    public String createTemporaryPassword() {
        Random random = new Random();
        int length = 8 + random.nextInt(5);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    public void sendTemporaryPassword(MemberEntity memberEntity, String temporaryPassword) throws  Exception{
        SimpleMailMessage authenticationMail = createAuthenticationMail(memberEntity.getEmail(), memberEntity.getName(), temporaryPassword);
        javaMailSender.send(authenticationMail);
    }

    public void checkCurrentPassword(String currentPassword, MemberEntity memberEntity) throws Exception {
        if (!passwordEncoder.matches(currentPassword, memberEntity.getPassword())) {
            throw new InvalidMemberPasswordException();
        }
    }

    public void checkSameNewPasswordAndCurrentPassword(PasswordUpdateRequestDto passwordUpdateRequestDto) throws Exception {
        if (passwordUpdateRequestDto.getNewPassword().equals(passwordUpdateRequestDto.getCurrentPassword())) {
            throw new PasswordChangeFailedException();
        }
    }

    public MemberEntity findByEmail(String email) throws Exception {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    public MemberEntity findById(Long memberId) throws Exception {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    public boolean checkJoinedEmail(String email) throws Exception {
        if (memberRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }
        return true;
    }

    private SimpleMailMessage createAuthenticationMail(String email, String name, String temporaryPassword) {
        SimpleMailMessage authenticationEmail = new SimpleMailMessage();
        authenticationEmail.setTo(email);
        authenticationEmail.setSubject("[FriendShip] 임시 비밀번호 발급");
        authenticationEmail.setText("안녕하세요, " + name + "님!\n요청하신 임시 비밀번호는 다음과 같습니다.\n\n" + "임시 비밀번호: " + temporaryPassword + "\n\n\n해당 비밀번호로 로그인 후 비밀번호를 변경해주세요.");
        return authenticationEmail;
    }


    // TODO: 탈퇴 한달 후 삭제
    public void deleteWithdrawnMember(MemberEntity memberEntity) {
        memberRepository.delete(memberEntity);
    }

    /**
     * 현재 로그인 회원 엔티티 조회
     */
    public MemberEntity getCurrentMemberEntity() throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String MemberEmail = userDetails.getUsername();
        MemberEntity memberEntity = findByEmail(MemberEmail);
        return memberEntity;
    }
}