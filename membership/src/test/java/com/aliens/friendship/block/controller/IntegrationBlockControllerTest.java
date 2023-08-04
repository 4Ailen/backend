package com.aliens.friendship.block.controller;

import com.aliens.db.chatting.entity.ChattingRoomEntity;
import com.aliens.db.chatting.repository.ChattingRoomRepository;
import com.aliens.db.emailauthentication.entity.EmailAuthenticationEntity;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

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
public class IntegrationBlockControllerTest {

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

    @Autowired
    ChattingRoomRepository chattingRoomRepository;



    EmailAuthenticationEntity mockEmailAuthenticationEntity;
    String BASIC_URL;
    String email;
    String password;
    JoinRequestDto joinRequestDto;
    MemberEntity memberEntity;

    @BeforeEach
    public void setUp() {
        //given
        BASIC_URL = "/api/v1/block";
        email = "test@example.com";
        password = "test1234";
        mockEmailAuthenticationEntity =
                EmailAuthenticationEntity.builder().
                        id("ddkls")
                        .email(email)
                        .createdAt(Instant.now())
                        .status(EmailAuthenticationEntity.Status.VERIFIED)
                        .expirationAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                        .build();
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
    @DisplayName("IntegrationController 사용자 차단 - 성공")
    void testBlockMember_Success() throws Exception {
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

        MemberEntity blockedMemberEntity = memberConverter.toMemberEntityWithUser(joinRequestDto);
        Long blockedMemberEntityId = memberService.register(blockedMemberEntity);

        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password));

        ChattingRoomEntity chattingRoomEntity = ChattingRoomEntity.builder()
                .status(ChattingRoomEntity.RoomStatus.OPEN)
                .build();
        chattingRoomRepository.save(chattingRoomEntity);

        Map<String, String> request = new HashMap<>();
        request.put("roomId",chattingRoomEntity.getId().toString());

        // when & then
        mockMvc.perform(post(BASIC_URL+ "/"+ blockedMemberEntityId)
                        .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                        .header("RefreshToken",tokenDto.getRefreshToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("blockMember",
                        requestFields(
                                fieldWithPath("roomId").description("차단할 채팅방 번호")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간")
                        )
                ));

    }
}
