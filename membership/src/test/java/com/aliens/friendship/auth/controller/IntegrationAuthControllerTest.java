package com.aliens.friendship.auth.controller;


import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.auth.business.AuthBusiness;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.member.controller.dto.JoinRequestDto;
import com.aliens.friendship.domain.member.converter.MemberConverter;
import com.aliens.friendship.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class IntegrationAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MemberConverter memberConverter;

    @Autowired
    AuthBusiness authBusiness;

    @Autowired
    MemberService memberService;


    String BASIC_URL;
    String email;
    String password;
    JoinRequestDto joinRequestDto;
    MemberEntity memberEntity;

    @BeforeEach
    public void setUp() {
        //given
        BASIC_URL = "/api/v1/auth";
        email = "test@example.com";
        password = "test1234";

        joinRequestDto =
                JoinRequestDto.builder()
                        .email(email)
                        .password(password)
                        .name("Aden")
                        .mbti(MemberEntity.Mbti.INTJ)
                        .gender("Male")
                        .nationality("USA")
                        .birthday("1990-01-01")
                        .selfIntroduction("Hello, I am Aden.")
                        .profileImage(createMockProfileImage())
                        .build();

        memberEntity = memberConverter.toMemberEntityWithUser(joinRequestDto);

    }

    private MockMultipartFile createMockProfileImage() {
        return new MockMultipartFile("profileImage", "profile.png", "image/png", new byte[0]);
    }

    @Test
    @DisplayName("IntegrationController 로그인 - 성공")
    void testLogin_Success() throws Exception {
        // Given
        memberService.register(memberEntity);
        LoginRequest loginRequest = LoginRequest.of("test@example.com", "test1234");

        // when & then
        mockMvc.perform(
                        post(BASIC_URL + "/authentication")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간"),
                                fieldWithPath("data.accessToken").description("accessToken"),
                                fieldWithPath("data.refreshToken").description("refreshToken")
                        )
                ));
    }

    @Test
    @DisplayName("IntegrationController 토큰재발급 - 실패(미만료된 토큰)")
    void testReissueToken_Success() throws Exception {
        // Given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(LoginRequest.of(email,password));


        // when & then
        mockMvc.perform(
                        post(BASIC_URL + "/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("reissueTokenFail",
                        responseFields(
                                fieldWithPath("status").description("HTTP 응답 상태 코드"),
                                fieldWithPath("code").description("API 에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("errors").description("에러 세부 정보"),
                                fieldWithPath("timestamp").description("에러 발생 시간")
                        )
                ));


    }
}
