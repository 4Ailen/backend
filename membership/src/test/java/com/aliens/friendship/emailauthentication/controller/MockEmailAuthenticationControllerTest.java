package com.aliens.friendship.emailauthentication.controller;

import com.aliens.friendship.domain.auth.filter.JwtAuthenticationFilter;
import com.aliens.friendship.domain.emailauthentication.business.EmailAuthenticationBusiness;
import com.aliens.friendship.domain.emailauthentication.controller.EmailAuthenticationController;
import com.aliens.db.emailauthentication.entity.EmailAuthenticationEntity;
import com.aliens.friendship.domain.emailauthentication.service.EmailAuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(EmailAuthenticationController.class)
class MockEmailAuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailAuthenticationService emailAuthenticationService;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    EmailAuthenticationBusiness emailAuthenticationBusiness;

    private static final String BASIC_URL = "/api/v1/email";

    @Test
    @DisplayName("MockController 이메일 전송 - 성공")
    void SendEmail_Success() throws Exception {
        // given
        String email = "test@case.com";
        doNothing().when(emailAuthenticationBusiness).sendAuthenticationEmail(email);

        // when & then
        mockMvc.perform(
                        post(BASIC_URL + "/{email}/verification", email)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("matching/getApplicant",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(emailAuthenticationBusiness, times(1)).sendAuthenticationEmail(email);
    }

    @Test
    @DisplayName("MockController 이메일 인증 - 성공")
    void VerifyEmail_Success() throws Exception {
        // given
        String email = "test@case.com";
        EmailAuthenticationEntity mockEmailAuthenticationEntity =
                EmailAuthenticationEntity.builder().id("ddkls").email(email).build();

        String token = mockEmailAuthenticationEntity.getId();
        MultiValueMap<String, String> tokenMap = new LinkedMultiValueMap<>();
        tokenMap.add("token", token);
        doNothing().when(emailAuthenticationBusiness).validateEmail(email, token);

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/{email}/verification", email)
                                .queryParam("token", token)
                )
                .andExpect(status().isOk())
                .andDo(print());

        verify(emailAuthenticationBusiness, times(1)).validateEmail(email, token);
    }

    @Test
    @DisplayName("인증 상태 요청 - 성공")
    void GetMemberAuthenticationStatus_Success() throws Exception {
        // given
        String email = "test@case.com";
        given(emailAuthenticationBusiness.getEmailAuthenticationStatus(email))
                .willReturn("AUTHENTICATED");

        // when & then
        mockMvc.perform(
                        get("/api/v1/email/{email}/authentication-status", email)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.status").value("AUTHENTICATED"));
//                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(emailAuthenticationBusiness, times(1)).getEmailAuthenticationStatus(anyString());
    }
}