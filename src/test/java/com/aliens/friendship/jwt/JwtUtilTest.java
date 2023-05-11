package com.aliens.friendship.jwt;

import com.aliens.friendship.domain.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.domain.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.global.config.security.CustomUserDetailService;
import com.aliens.friendship.domain.jwt.domain.dto.LoginDto;
import com.aliens.friendship.domain.jwt.domain.dto.TokenDto;
import com.aliens.friendship.domain.jwt.util.JwtTokenUtil;
import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.domain.Nationality;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import com.aliens.friendship.domain.member.repository.NationalityRepository;
import com.aliens.friendship.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class JwtUtilTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EmailAuthenticationRepository emailAuthenticationRepository;

    JoinDto memberJoinRequest;

    @Autowired
    NationalityRepository nationalityRepository;

    @BeforeEach
    public void setupMember() throws Exception {
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        Nationality nationality = Nationality.builder().id(1).nationalityText("korean").build();
        memberJoinRequest = JoinDto.builder()
                .password("1q2w3e4r")
                .email("test@case.com")
                .name("김명준")
                .mbti("INTJ")
                .birthday("1998-09-21")
                .profileImage(mockMultipartFile)
                .gender("MALE")
                .nationality(nationality)
                .build();
        EmailAuthentication emailAuthentication = EmailAuthentication.createEmailAuthentication(memberJoinRequest.getEmail());
        emailAuthentication.updateStatus(EmailAuthentication.Status.VERIFIED);
        emailAuthenticationRepository.save(emailAuthentication);
        memberService.join(memberJoinRequest);
    }


    @DisplayName("토큰 생성 검사")
    @Test
    void createToken() {
        //when
        String token = jwtTokenUtil.generateAccessToken(memberJoinRequest.getEmail());

        //then
        assertThat(token).isNotEmpty();
    }


    @DisplayName("토큰에서 username(email) 추출")
    @Test
    void extractUsernameFromToken() {
        //given
        String token = jwtTokenUtil.generateAccessToken(memberJoinRequest.getEmail());

        //when
        String username = jwtTokenUtil.getEmail(token);

        //then
        assertThat(username).isEqualTo(memberJoinRequest.getEmail());
    }


    @DisplayName("토큰 유효성 검증 (유효 토큰)")
    @Test
    public void validateTokenHasValidity() throws Exception {
        //given
        LoginDto loginMember = new LoginDto(memberJoinRequest.getEmail(),"1q2w3e4r");
        String validatedToken = memberService.login(loginMember).getAccessToken();
        UserDetails userDetails = customUserDetailService.loadUserByUsername(loginMember.getEmail());

        //when
        boolean isValidToken = jwtTokenUtil.validateToken(validatedToken, userDetails);

        //then
        assertThat(isValidToken).isTrue();
    }


    @DisplayName("토큰 유효성 검증 (메일이 다른 토큰)")
    @Test
    public void validateTokenHasDifferentEmail() throws Exception {
        //given
        LoginDto loginMember = new LoginDto(memberJoinRequest.getEmail(),"1q2w3e4r");
        memberService.login(loginMember);

        String invalidatedToken = jwtTokenUtil.generateAccessToken("FakeMail");
        UserDetails userDetails = customUserDetailService.loadUserByUsername(loginMember.getEmail());

        //when
        boolean isinValidToken = jwtTokenUtil.validateToken(invalidatedToken, userDetails);

        //then
        assertThat(isinValidToken).isFalse();
    }


    @DisplayName("토큰 유효성 검증 (로그아웃한 토큰)")
    @Test
    public void validateTokenHasLogout() throws Exception {
        //given
        LoginDto loginMember = new LoginDto(memberJoinRequest.getEmail(),"1q2w3e4r");
        TokenDto validatedToken = memberService.login(loginMember);

        //when
        String username = jwtTokenUtil.getEmail(validatedToken.getAccessToken());
        memberService.logout(TokenDto.of("Bearer "+validatedToken.getAccessToken(), validatedToken.getRefreshToken()), username);

        //then
        UserDetails userDetails = customUserDetailService.loadUserByUsername(loginMember.getEmail());
        boolean isinValidToken = jwtTokenUtil.validateToken(validatedToken.getRefreshToken(), userDetails);
        assertThat(isinValidToken).isTrue();

    }

}

