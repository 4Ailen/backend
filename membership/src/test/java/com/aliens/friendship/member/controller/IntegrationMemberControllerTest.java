package com.aliens.friendship.member.controller;

import com.aliens.db.emailauthentication.entity.EmailAuthenticationEntity;
import com.aliens.db.emailauthentication.repository.EmailAuthenticationRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.auth.business.AuthBusiness;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.member.controller.dto.JoinRequestDto;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationMemberControllerTest {

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


    @Autowired
    private EmailAuthenticationRepository emailAuthenticationRepository;

    EmailAuthenticationEntity mockEmailAuthenticationEntity;
    String BASIC_URL;
    String email;
    String password;
    String fcmToken;
    JoinRequestDto joinRequestDto;
    MemberEntity memberEntity;

    @BeforeEach
    public void setUp() {
        //given
        BASIC_URL = "/api/v1/member";
        email = "test@example.com";
        password = "test1234";
        fcmToken = "testFcmToken";

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
    @DisplayName("IntegrationController 회원 가입 - 성공")
    void testJoinMember_Success() throws Exception {
        // Given
        emailAuthenticationRepository.save(mockEmailAuthenticationEntity);
        joinRequestDto = JoinRequestDto.builder()
                .email(email)
                .password(password)
                .name("Aden")
                .mbti(MemberEntity.Mbti.INTJ)
                .gender("Male")
                .nationality("KOREAN")
                .birthday("1990-01-01")
                .selfIntroduction("Hello, I am Aden.")
                .profileImage(createMockProfileImage())
                .build();

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(BASIC_URL)
                        .file("profileImage", createMockProfileImage().getBytes())
                        .param("email", joinRequestDto.getEmail())
                        .param("password", joinRequestDto.getPassword())
                        .param("name", joinRequestDto.getName())
                        .param("mbti", joinRequestDto.getMbti().name())
                        .param("gender", joinRequestDto.getGender())
                        .param("nationality", joinRequestDto.getNationality())
                        .param("birthday", joinRequestDto.getBirthday())
                        .param("selfIntroduction", joinRequestDto.getSelfIntroduction())
        );

        // Then
        resultActions.andExpect(status().isOk());
    }


    @Test
    @DisplayName("IntegrationController 사용자 정보 조회 - 성공")
    void testGetMemberInfo_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password),fcmToken);

        // when & then
        mockMvc.perform(get(BASIC_URL+ "/email/"+email+"/existence")
                        .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                        .header("RefreshToken",tokenDto.getRefreshToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 조회하였습니다."));

    }


    @Test
    @DisplayName("IntegrationController 회원 탈퇴 - 성공")
    void testMemberWithdrawal_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password),fcmToken);


        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put("password", password);

        mockMvc.perform(
                        delete(BASIC_URL + "/withdraw")
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(passwordMap)
                                )
                        )
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/email/{email}/existence", email)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.existence").value(true));
    }


    @Test
    @DisplayName("IntegrationController 회원 이메일 조회 - 성공")
    void testMemberEmail_Success() throws Exception {
        //given
        memberService.register(memberEntity);

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/email/{email}/existence", email)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.existence").value(true));
    }


    @Test
    @DisplayName("IntegrationController 회원 임시 비밀번호 발급 요청 - 성공")
    void IssueTemporaryPassword_Success() throws Exception {
        // given
        memberService.register(memberEntity);

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("name", "Aden");

        // when & then
        mockMvc.perform(
                        post(BASIC_URL + "/{email}/password/temp", email)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(nameMap))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }


    @Test
    @DisplayName("IntegrationController 비밀번호 변경 요청 - 성공")
    void ChangePassword_Success() throws Exception {
        // given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password),fcmToken);

        PasswordUpdateRequestDto passwordUpdateRequestDto =
                PasswordUpdateRequestDto.builder()
                        .currentPassword("test1234")
                        .newPassword("newnew").build();


        // when & then
        mockMvc.perform(
                        put(BASIC_URL + "/password")
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(passwordUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
    @Test
    @DisplayName("IntegrationController 프로필 자기소개 변경 요청 - 성공")
    void ChangeSelfIntroduction_Success() throws Exception {
        // given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password),fcmToken);
        String selfIntroductionChangeRequest = "반가워요!";

        // when & then
        mockMvc.perform(
                        put(BASIC_URL + "/self-introduction")
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(selfIntroductionChangeRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }


    @Test
    @DisplayName("IntegrationController프로필 이미지 수정 요청 - 성공")
    void ChangeProfileImage_Success() throws Exception {
        // given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password),fcmToken);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(HttpMethod.PUT,BASIC_URL+"/profile-image")
                        .file("profileImage",createMockProfileImage().getBytes())
                        .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                        .header("RefreshToken",tokenDto.getRefreshToken())
        );

        // Then
        resultActions.andExpect(status().isOk());

    }











}
