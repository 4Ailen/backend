package com.aliens.friendship.emailAuthentication.controller;

import com.aliens.friendship.domain.auth.filter.JwtAuthenticationFilter;
import com.aliens.friendship.domain.emailAuthentication.controller.EmailAuthenticationController;
import com.aliens.friendship.domain.emailAuthentication.domain.EmailAuthentication;
import com.aliens.friendship.domain.emailAuthentication.service.EmailAuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(EmailAuthenticationController.class)
class EmailAuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailAuthenticationService emailAuthenticationService;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String BASIC_URL = "/api/v1/email";

    @Test
    @DisplayName("이메일 전송 성공")
    void SendEmail_Success() throws Exception {
        // given
        String email = "test@case.com";
        doNothing().when(emailAuthenticationService).sendEmail(email);

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

        verify(emailAuthenticationService, times(1)).sendEmail(email);
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void VerifyEmail_Success() throws Exception {
        // given
        String email = "test@case.com";
        EmailAuthentication mockEmailAuthentication = EmailAuthentication.createEmailAuthentication(email);
        String token = mockEmailAuthentication.getId();
        MultiValueMap<String, String> tokenMap = new LinkedMultiValueMap<>();
        tokenMap.add("token", token);
        doNothing().when(emailAuthenticationService).validateEmail(email, token);

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/{email}/verification", email)
                                .queryParam("token", token)
                )
                .andExpect(status().isOk())
                .andDo(print());

        verify(emailAuthenticationService, times(1)).validateEmail(email, token);
    }
}