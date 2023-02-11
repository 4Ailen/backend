package com.aliens.friendship.jwt;

import com.aliens.friendship.global.config.security.CustomUserDetailService;
import com.aliens.friendship.jwt.domain.dto.JoinDto;
import com.aliens.friendship.jwt.domain.dto.LoginDto;
import com.aliens.friendship.jwt.domain.dto.TokenDto;
import com.aliens.friendship.jwt.util.JwtTokenUtil;
import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.repository.MemberRepository;
import com.aliens.friendship.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JwtUtilTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    JoinDto memberJoinRequest;

    @BeforeEach
    public void setupMember() {
        Nationality nationality = Nationality.builder().id(1).natinalityText("korean").build();
        memberJoinRequest = JoinDto.builder()
                .password("1q2w3e4r")
                .email("skatks1016@naver.com")
                .name("김명준")
                .mbti("INTJ")
                .age(21)
                .gender("male")
                .nationality(nationality)
                .build();
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
        String username = jwtTokenUtil.getUsername(token);

        //then
        assertThat(username).isEqualTo(memberJoinRequest.getEmail());
    }


    @DisplayName("토큰 유효성 검증 (유효 토큰)")
    @Test
    public void validateTokenHasValidity() {
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
    public void validateTokenHasDifferentEmail() {
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
    public void validateTokenHasLogout() throws InterruptedException {
        //given
        LoginDto loginMember = new LoginDto(memberJoinRequest.getEmail(),"1q2w3e4r");
        TokenDto validatedToken = memberService.login(loginMember);

        //when
        String username = jwtTokenUtil.getUsername(validatedToken.getAccessToken());
        memberService.logout(TokenDto.of("Bearer "+validatedToken.getAccessToken(), validatedToken.getRefreshToken()), username);

        //then
        UserDetails userDetails = customUserDetailService.loadUserByUsername(loginMember.getEmail());
        boolean isinValidToken = jwtTokenUtil.validateToken(validatedToken.getRefreshToken(), userDetails);
        assertThat(isinValidToken).isTrue();

    }

}

