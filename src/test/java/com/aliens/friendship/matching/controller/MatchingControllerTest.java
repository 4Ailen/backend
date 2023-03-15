package com.aliens.friendship.matching.controller;


import com.aliens.friendship.jwt.domain.dto.LoginDto;
import com.aliens.friendship.jwt.domain.dto.TokenDto;
import com.aliens.friendship.matching.service.BlockingInfoService;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.repository.MemberRepository;
import com.aliens.friendship.member.repository.NationalityRepository;
import com.aliens.friendship.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class MatchingControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BlockingInfoService blockingInfoService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NationalityRepository nationalityRepository;

    TokenDto token;
    JoinDto blockingMember;
    JoinDto blockedMember;
    int idOfBlockedMember;

    @BeforeEach
    @Transactional
    public void setupLoginMember() throws Exception{

        //회원가입
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        Nationality nationality = new Nationality(1, "South Korea");
        nationalityRepository.save(nationality);
        blockingMember = JoinDto.builder()
                .password("1q2w3e4r")
                .email("skatks1016@naver.com")
                .name("김명준")
                .mbti("INTJ")
                .birthday("1998-01-01")
                .gender("male")
                .image(mockMultipartFile)
                .nationality(nationality)
                .build();

        blockedMember = JoinDto.builder()
                .password("1q2w3e4r")
                .email("skatks1125@naver.com")
                .name("최정은")
                .mbti("INTJ")
                .birthday("1998-01-01")
                .gender("male")
                .image(mockMultipartFile)
                .nationality(nationality)
                .build();

        memberService.join(blockedMember);
        memberService.join(blockingMember);

        //blockedMember의 Id값 추출
        Optional<Member> blockedMemberEntity = memberRepository.findByEmail(blockedMember.getEmail());
        idOfBlockedMember = blockedMemberEntity.get().getId();


    }

    @Test
    @DisplayName("차단 성공")
    void Blocking_Success() throws Exception {

        // given
        LoginDto loginMember = new LoginDto("skatks1016@naver.com","1q2w3e4r");
        TokenDto tokenResponse = memberService.login(loginMember);
        String accessToken = tokenResponse.getAccessToken();

        // when & then
        mockMvc.perform(get("/matching/partner/" + idOfBlockedMember+ "/block")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

}
