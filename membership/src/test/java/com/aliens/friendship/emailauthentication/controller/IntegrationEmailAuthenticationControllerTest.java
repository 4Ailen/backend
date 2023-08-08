package com.aliens.friendship.emailauthentication.controller;

import com.aliens.db.emailauthentication.entity.EmailAuthenticationEntity;
import com.aliens.db.emailauthentication.repository.EmailAuthenticationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class IntegrationEmailAuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailAuthenticationRepository emailAuthenticationRepository;

    String BASIC_URL;
    EmailAuthenticationEntity mockEmailAuthenticationEntity;
    String email;

    @BeforeEach
    public void getAccessToken() throws Exception {
        //given
        BASIC_URL = "/api/v1/email";
        email = "test@case.com";

        mockEmailAuthenticationEntity =
                EmailAuthenticationEntity.builder().
                        id("ddkls")
                        .email(email)
                        .status(EmailAuthenticationEntity.Status.NOT_VERIFIED)
                        .createdAt(Instant.now())
                        .expirationAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                        .build();

    }

    @Test
    @DisplayName("IntegrationController 이메일 전송 - 성공 ")
    public void SendEmail() throws Exception {

        // given
        email = "test@case.com";

        // when & then
        mockMvc.perform(
                        post(BASIC_URL + "/{email}/verification", email)
                )
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("sendEmail",
                        pathParameters(parameterWithName("email").description("이메일")),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간")
                        )
                ));

    }

    @Test
    @DisplayName("IntegrationController 이메일 인증 - 성공")
    void VerifyEmail_Success() throws Exception {
        // given
        String token = mockEmailAuthenticationEntity.getId();
        emailAuthenticationRepository.save(mockEmailAuthenticationEntity);

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/{email}/verification", email)
                                .queryParam("token", token)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("emailVerificationComplete"))
                .andDo(document("verifyEmail",
                        pathParameters(parameterWithName("email").description("이메일")),
                        requestParameters(parameterWithName("token").description("토큰"))
                ));

    }


    @Test
    @DisplayName("IntegrationController 인증 상태 요청 - 성공")
    void GetMemberAuthenticationStatus_Success() throws Exception {
        // given
        mockEmailAuthenticationEntity.updateStatus(EmailAuthenticationEntity.Status.VERIFIED);
        emailAuthenticationRepository.save(mockEmailAuthenticationEntity);

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/{email}/authentication-status", email)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("AUTHENTICATED"))
                .andDo(document("getMemberAuthenticationStatus",
                        pathParameters(parameterWithName("email").description("이메일")),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간"),
                                fieldWithPath("data.status").description("이메일 인증 상태")
                        )
                ));

    }


}