package com.aliens.friendship.domain.member.service;

import com.aliens.friendship.domain.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.domain.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.domain.jwt.repository.LogoutAccessTokenRedisRepository;
import com.aliens.friendship.domain.jwt.repository.RefreshTokenRedisRepository;
import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.domain.member.exception.*;
import com.aliens.friendship.global.config.cache.CacheKey;
import com.aliens.friendship.global.config.jwt.JwtExpirationEnums;
import com.aliens.friendship.domain.jwt.domain.LogoutAccessToken;
import com.aliens.friendship.domain.jwt.domain.RefreshToken;
import com.aliens.friendship.domain.jwt.domain.dto.LoginDto;
import com.aliens.friendship.domain.jwt.domain.dto.TokenDto;
import com.aliens.friendship.domain.jwt.util.JwtTokenUtil;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ProfileImageService profileImageService;
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final JavaMailSender javaMailSender;
    @Value("${spring.domain}")
    private String domainUrl;

    public void join(JoinDto joinDto) throws Exception {
        checkDuplicatedEmail(joinDto.getEmail());
        checkEmailAuthentication(joinDto.getEmail());
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

    public TokenDto login(LoginDto loginDto) throws Exception {
        Member member = memberRepository.findByEmail(loginDto.getEmail()).orElseThrow(MemberNotFoundException::new);
        checkWithdrawn(member.getStatus());
        checkPassword(loginDto.getPassword(), member.getPassword());
        String email = member.getEmail();
        String accessToken = jwtTokenUtil.generateAccessToken(email);
        RefreshToken refreshToken = saveRefreshToken(email);
        return TokenDto.of(accessToken, refreshToken.getRefreshToken());
    }

    public boolean isJoinedEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    private void checkWithdrawn(Member.Status status) throws Exception {
        if (status == Member.Status.WITHDRAWN) {
            throw new MemberNotFoundException();
        }
    }

    private void checkPassword(String rawPassword, String findMemberPassword) {
        if (!passwordEncoder.matches(rawPassword, findMemberPassword)) {
            throw new InvalidMemberPasswordException();
        }
    }

    private RefreshToken saveRefreshToken(String email) {
        return refreshTokenRedisRepository.save(RefreshToken.createRefreshToken(email,
                jwtTokenUtil.generateRefreshToken(email), JwtExpirationEnums.REFRESH_TOKEN_EXPIRATION_TIME.getValue()));
    }

    @Transactional(readOnly = true)
    public MemberInfoDto getMemberInfo() throws Exception {
        String email = getCurrentMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        return MemberInfoDto.builder()
                .email(member.getEmail())
                .mbti(member.getMbti())
                .gender(member.getGender())
                .nationality(member.getNationality().getCountryImageUrl())
                .birthday(member.getBirthday())
                .age(member.getAge())
                .name(member.getName())
                .profileImage(domainUrl + System.getProperty("user.dir") + member.getProfileImageUrl())
                .build();
    }

    @CacheEvict(value = CacheKey.USER, key = "#username")
    public void logout(TokenDto tokenDto, String username) {
        String accessToken = resolveToken(tokenDto.getAccessToken());
        long remainMilliSeconds = jwtTokenUtil.getRemainMilliSeconds(accessToken);
        refreshTokenRedisRepository.deleteById(username);
        logoutAccessTokenRedisRepository.save(LogoutAccessToken.of(accessToken, username, remainMilliSeconds));
    }

    public String resolveToken(String token) {
        return token.substring(7);
    }

    public TokenDto reissue(String refreshToken) {
        refreshToken = resolveToken(refreshToken);
        String email = getCurrentMemberEmail();
        RefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(email).orElseThrow(NoSuchElementException::new);

        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            return reissueRefreshToken(refreshToken, email);
        }
        throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
    }

    private TokenDto reissueRefreshToken(String refreshToken, String email) {
        if (lessThanReissueExpirationTimesLeft(refreshToken)) {
            String accessToken = jwtTokenUtil.generateAccessToken(email);
            return TokenDto.of(accessToken, saveRefreshToken(email).getRefreshToken());
        }
        return TokenDto.of(jwtTokenUtil.generateAccessToken(email), refreshToken);
    }

    private boolean lessThanReissueExpirationTimesLeft(String refreshToken) {
        return jwtTokenUtil.getRemainMilliSeconds(refreshToken) < JwtExpirationEnums.REISSUE_EXPIRATION_TIME.getValue();
    }

    private String getCurrentMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private void checkDuplicatedEmail(String email) throws Exception {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new DuplicateMemberEmailException();
        }
    }

    private void checkEmailAuthentication(String email) throws Exception {
        EmailAuthentication emailAuthentication = emailAuthenticationRepository.findByEmail(email);
        if (emailAuthentication.getStatus() == EmailAuthentication.Status.NOT_VERIFIED) {
            throw new EmailVerificationException();
        }
    }

    public void issueTemporaryPassword(String email, String name) throws Exception {
        Member member = memberRepository.findByEmailAndName(email, name)
                .orElseThrow(MemberNotFoundException::new);
        String temporaryPassword = createTemporaryPassword();
        member.updatePassword(passwordEncoder.encode(temporaryPassword));
        memberRepository.save(member);
        SimpleMailMessage authenticationMail = createAuthenticationMail(member.getEmail(), member.getName(), temporaryPassword);
        javaMailSender.send(authenticationMail);
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

    public void changeProfileNameAndMbti(String name, String mbti) {
        Member member = memberRepository.findByEmail(getCurrentMemberEmail()).orElseThrow(MemberNotFoundException::new);
        member.updateName(name);
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
        String status = emailAuthentication.getStatus().toString();
        if (status.equals("VERIFIED")) {
            return "AUTHENTICATED";
        } else {
            return "NOT_AUTHENTICATED";
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
                .nationality(member.getNationality().getNationalityText())
                .birthday(member.getBirthday())
                .age(member.getAge())
                .name(member.getName())
                .build();
    }
}