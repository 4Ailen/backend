package com.aliens.friendship.chat.controller;

import com.aliens.friendship.domain.auth.filter.JwtAuthenticationFilter;
import com.aliens.friendship.domain.auth.service.AuthService;
import com.aliens.friendship.domain.chat.business.ChatBusiness;
import com.aliens.friendship.domain.chat.controller.ChattingController;
import com.aliens.friendship.domain.chat.service.ChatService;
import com.aliens.friendship.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ChattingController.class)
class MockIChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private ChatService chattingService;

    @MockBean
    private ChatBusiness chatBusiness;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("채팅토큰 발급 컨트롤러 요청 - 성공")
    void getJWTTokenForChatting_Success() throws Exception {
        // given
        String testToken= "gdasgasgdasg";
        when(chatBusiness.getJwtForChattingServer()).thenReturn(testToken);

        // when & then
        ResultActions resultActions = mockMvc.perform(get("/api/v1/chat/token")
                        .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        verify(chatBusiness, times(1)).getJwtForChattingServer();
    }

}