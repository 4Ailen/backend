package com.aliens.friendship.chatting.controller;

import com.aliens.friendship.domain.auth.dto.RoomInfoDto;
import com.aliens.friendship.domain.auth.filter.JwtAuthenticationFilter;
import com.aliens.friendship.domain.auth.service.AuthService;
import com.aliens.friendship.domain.chatting.controller.ChattingController;
import com.aliens.friendship.domain.chatting.service.ChattingService;
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
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ChattingController.class)
class ChattingControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private ChattingService chattingService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("채팅토큰 발급 컨트롤러 요청 성공")
    void getJWTTokenForChatting_Success() throws Exception {
        // given
        String testToken= "gdasgasgdasg";
        when(chattingService.getJWTTokenForChatting()).thenReturn(testToken);

        // when & then
        ResultActions resultActions = mockMvc.perform(get("/api/v1/chat/token")
                        .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        verify(chattingService, times(1)).getJWTTokenForChatting();
    }

    @Test
    @DisplayName("채팅토큰 발급 컨트롤러 요청 성공")
    void getRooms_Success() throws Exception {
        // given
        RoomInfoDto roomInfoDto1 = new RoomInfoDto(1L,"OPEN",1);
        RoomInfoDto roomInfoDto2 = new RoomInfoDto(2L,"OPEN",2);
        List<RoomInfoDto> roomInfoDtos = Arrays.asList(roomInfoDto1,roomInfoDto2);
        when(chattingService.getRoomInfoDtoListByMatchingCurrentMemberId()).thenReturn(roomInfoDtos);

        // when & then
        ResultActions resultActions = mockMvc.perform(get("/api/v1/chat/room")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        verify(chattingService, times(1)).getRoomInfoDtoListByMatchingCurrentMemberId();
    }


}