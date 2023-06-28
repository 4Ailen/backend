package com.aliens.friendship.domain.member.service;

import com.aliens.friendship.domain.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.domain.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.exception.*;
import com.aliens.friendship.domain.member.repository.MemberRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageService profileImageService;
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final JavaMailSender javaMailSender;

    @Value("${file-server.domain}")
    private String domainUrl;

    public void join(JoinDto joinDto) throws Exception {
        checkDuplicatedAndWithdrawnInAWeekEmail(joinDto.getEmail());
        checkEmailAuthentication(joinDto.getEmail());
        emailAuthenticationRepository.deleteByEmail(joinDto.getEmail());
        joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        joinDto.setImageUrl(profileImageService.uploadProfileImage(joinDto.getProfileImage()));
        memberRepository.save(Member.ofUser(joinDto));
    }

    public void joinAdmin(JoinDto joinDto) {
        joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        memberRepository.save(Member.ofAdmin(joinDto));
    }

    public void withdraw(String password) throws Exception {
        String email = getCurrentMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidMemberPasswordException();
        }
        member.updateStatus(Member.Status.WITHDRAWN);
        member.updateWithdrawalDate(getCurrentDate());
    }

    private String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }

    // TODO: 탈퇴 일주일 후 삭제
    public void deleteWithdrawnMember(Member member) {
        memberRepository.delete(member);
    }

    public void deleteMemberInfoByAdmin(Integer memberId) {
        memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        memberRepository.deleteById(memberId);
    }

    public boolean isJoinedEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    public MemberInfoDto getMemberInfo() throws Exception {
        String email = getCurrentMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        return MemberInfoDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .mbti(member.getMbti())
                .gender(member.getGender())
                .nationality(member.getNationality())
                .birthday(member.getBirthday())
                .age(member.getAge())
                .name(member.getName())
                .profileImage(domainUrl + member.getProfileImageUrl())
                .build();
    }

    private String getCurrentMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private void checkDuplicatedAndWithdrawnInAWeekEmail(String email) throws Exception {
        if (memberRepository.findByEmail(email).isPresent()) {
            if (memberRepository.findByEmail(email).get().getStatus() == Member.Status.WITHDRAWN) {
                throw new WithdrawnMemberWithinAWeekException();
            } else {
                throw new DuplicateMemberEmailException();
            }
        }
    }

    private void checkEmailAuthentication(String email) throws Exception {
        EmailAuthentication emailAuthentication = emailAuthenticationRepository.findByEmail(email);
        if (emailAuthentication.getStatus() == EmailAuthentication.Status.NOT_VERIFIED) {
            throw new EmailVerificationException();
        }
    }

    public void issueTemporaryPassword(String email, String name) throws Exception {
        if (memberRepository.findByEmailAndName(email, name).isEmpty()) {
            if (memberRepository.findByEmail(email).isEmpty()) {
                throw new MemberNotFoundException();
            } else {
                throw new InvalidMemberNameException();
            }
        } else {
            Member member = memberRepository.findByEmailAndName(email, name).get();
            String temporaryPassword = createTemporaryPassword();
            member.updatePassword(passwordEncoder.encode(temporaryPassword));
            memberRepository.save(member);
            SimpleMailMessage authenticationMail = createAuthenticationMail(member.getEmail(), member.getName(), temporaryPassword);
            javaMailSender.send(authenticationMail);
        }
    }

    private String createTemporaryPassword() {
        Random random = new Random();
        int length = 8 + random.nextInt(5);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    private SimpleMailMessage createAuthenticationMail(String email, String name, String temporaryPassword) {
        SimpleMailMessage authenticationEmail = new SimpleMailMessage();
        authenticationEmail.setTo(email);
        authenticationEmail.setSubject("[FriendShip] 임시 비밀번호 발급");
        authenticationEmail.setText("안녕하세요, " + name + "님!\n요청하신 임시 비밀번호는 다음과 같습니다.\n\n" + "임시 비밀번호: " + temporaryPassword + "\n\n\n해당 비밀번호로 로그인 후 비밀번호를 변경해주세요.");
        return authenticationEmail;
    }

    public void changePassword(PasswordUpdateRequestDto passwordUpdateRequestDto) throws Exception {
        Member member = memberRepository.findByEmail(getCurrentMemberEmail()).orElseThrow(MemberNotFoundException::new);
        checkCurrentPassword(passwordUpdateRequestDto.getCurrentPassword(), member);
        checkNewPassword(passwordUpdateRequestDto);
        member.updatePassword(passwordEncoder.encode(passwordUpdateRequestDto.getNewPassword()));
        memberRepository.save(member);
    }

    private void checkCurrentPassword(String currentPassword, Member member) throws Exception {
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new InvalidMemberPasswordException();
        }
    }

    private void checkNewPassword(PasswordUpdateRequestDto passwordUpdateRequestDto) throws Exception {
        if (passwordUpdateRequestDto.getNewPassword().equals(passwordUpdateRequestDto.getCurrentPassword())) {
            throw new PasswordChangeFailedException();
        }
    }

    public void changeProfileNameAndMbti(Member.Mbti mbti) {
        Member member = memberRepository.findByEmail(getCurrentMemberEmail()).orElseThrow(MemberNotFoundException::new);
        member.updateMbti(mbti);
        memberRepository.save(member);
    }

    public void changeProfileImage(MultipartFile profileImage) throws Exception {
        Member member = memberRepository.findByEmail(getCurrentMemberEmail()).orElseThrow(MemberNotFoundException::new);
        if (!member.getProfileImageUrl().equals("/default_image.jpg")) {
            profileImageService.deleteProfileImage(member.getProfileImageUrl());
        }
        member.updateImageUrl(profileImageService.uploadProfileImage(profileImage));
        memberRepository.save(member);
    }

    public String getMemberAuthenticationStatus(String email) {
        EmailAuthentication emailAuthentication = emailAuthenticationRepository.findByEmail(email);
        if (emailAuthentication == null) {
            if (isJoinedEmail(email)) {
                return "JOINED"; // 이메일 인증 후 회원가입된 상태
            } else {
                return "EMAIL_NOT_SENT"; // 이메일 전송 요청 필요
            }
        }
        String status = emailAuthentication.getStatus().toString();
        if (status.equals("VERIFIED")) {
            if (Instant.now().isAfter(emailAuthentication.getExpirationTime())) {
                emailAuthenticationRepository.deleteByEmail(email);
                return "REAUTHENTICATION_REQUIRED"; // 이메일 인증이 되었으나, 기간 만료로 재인증이 필요
            } else {
                return "AUTHENTICATED"; // 이메일 인증 완료
            }
        } else {
            return "EMAIL_SENT_NOT_AUTHENTICATED"; // 이메일 인증 미완료
        }
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) throws Exception {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public MemberInfoDto getMemberInfoByMemberId(Integer memberId) throws Exception {
        String email = getCurrentMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        return MemberInfoDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .mbti(member.getMbti())
                .gender(member.getGender())
                .nationality(member.getNationality())
                .birthday(member.getBirthday())
                .age(member.getAge())
                .name(member.getName())
                .build();
    }
}