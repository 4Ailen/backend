package com.aliens.friendship.report.controller;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.report.ReportCategory;
import com.aliens.friendship.domain.auth.business.AuthBusiness;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.member.controller.dto.JoinRequestDto;
import com.aliens.friendship.domain.member.controller.dto.ReportRequestDto;
import com.aliens.friendship.domain.member.converter.MemberConverter;
import com.aliens.friendship.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MemberConverter memberConverter;

    @Autowired
    MemberService memberService;

    @Autowired
    AuthBusiness authBusiness;



    String BASIC_URL;
    String email;
    String password;
    JoinRequestDto joinRequestDto;
    MemberEntity memberEntity;

    @BeforeEach
    public void setUp() {
        //given
        BASIC_URL = "/api/v1/report";
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
    @DisplayName("IntegrationController 사용자 신고 - 성공")
    void testReportMember_Success() throws Exception {
        //given
        memberService.register(memberEntity);

        joinRequestDto =
                JoinRequestDto.builder()
                        .email("sad")
                        .password(password)
                        .name("Joy")
                        .mbti(MemberEntity.Mbti.INTJ)
                        .gender("Male")
                        .nationality("USA")
                        .birthday("1990-01-01")
                        .selfIntroduction("Hello, I am Aden.")
                        .profileImage(createMockProfileImage())
                        .build();

        MemberEntity ReportedMemberEntity = memberConverter.toMemberEntityWithUser(joinRequestDto);
        Long reportedMemberEntityId = memberService.register(ReportedMemberEntity);

        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password));

        ReportRequestDto reportRequestDto = ReportRequestDto.builder()
                .reportCategory(ReportCategory.SPAM)
                .reportContent("spam")
                .build();

        // when & then
        mockMvc.perform(
                        post(BASIC_URL+ "/"+ reportedMemberEntityId.toString())
                            .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                            .header("RefreshToken",tokenDto.getRefreshToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(reportRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("신고 완료"))
                .andExpect(jsonPath("$.timestamp").exists());

    }

}