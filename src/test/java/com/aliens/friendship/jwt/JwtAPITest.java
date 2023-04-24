package com.aliens.friendship.jwt;

import com.aliens.friendship.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.jwt.domain.dto.LoginDto;
import com.aliens.friendship.jwt.domain.dto.TokenDto;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.repository.NationalityRepository;
import com.aliens.friendship.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@Transactional
public class JwtAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailAuthenticationRepository emailAuthenticationRepository;
    
    @Autowired
    private NationalityRepository nationalityRepository;

    @BeforeEach
    public void setupMember() throws Exception {
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        Nationality nationality = Nationality.builder().id(1).nationalityText("Korean").build();
        JoinDto memberJoinRequest = JoinDto.builder()
                .password("1q2w3e4r")
                .email("test@case.com")
                .name("김명준")
                .mbti("INTJ")
                .birthday("1998-01-01")
                .gender("male")
                .nationality(nationality)
                .profileImage(mockMultipartFile)
                .build();
        EmailAuthentication emailAuthentication = EmailAuthentication.createEmailAuthentication(memberJoinRequest.getEmail());
        emailAuthentication.updateStatus(EmailAuthentication.Status.VERIFIED);
        emailAuthenticationRepository.save(emailAuthentication);
        memberService.join(memberJoinRequest);
    }


    @DisplayName("로그인 시도 /login 접근")
    @Test
    public void MemberAPIAccessToLogin() throws Exception {
        //given
        final String url = "/login";
        LoginDto loginMember = new LoginDto("test@case.com","1q2w3e4r");

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMember))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("로그인 유지 /health 접근(with토큰)")
    @Test
    public void MemberAPIAccessWithToken() throws Exception {
        //given
        final String url = "/health";
        LoginDto loginMember = new LoginDto("test@case.com","1q2w3e4r");
        TokenDto tokenResponse = memberService.login(loginMember);

        //when
        ResultActions resultActions = mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + tokenResponse.getAccessToken()))
                .andExpect(status().isOk());


        // then
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("로그인 유지 /health 접근(without 토큰)")
    @Test
    public void MemberAPIAccessWithoutToken() throws Exception {
        //given
        final String url = "/health";
        LoginDto loginMember = new LoginDto("test@case.com","1q2w3e4r");

        //when
        ResultActions resultActions = mockMvc.perform(get(url)).andExpect(status().is4xxClientError());

        // then
        resultActions.andExpect(status().is4xxClientError());

    }

    @DisplayName("로그 아웃 /logout 접근(with 토큰)")
    @Test
    public void MemberAPIAccessToLogout() throws Exception {
        //given
        final String url = "/logout";
        LoginDto loginMember = new LoginDto("test@case.com","1q2w3e4r");
        TokenDto tokenResponse = memberService.login(loginMember);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenResponse.getAccessToken())
                        .header("RefreshToken",tokenResponse.getRefreshToken()))
                .andExpect(status().isOk());

        // then
        resultActions.andExpect(status().isOk());

    }


}


