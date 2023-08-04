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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
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
    JoinRequestDto joinRequestDto;
    MemberEntity memberEntity;

    @BeforeEach
    public void setUp() {
        //given
        BASIC_URL = "/api/v1/member";
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

        // When && then
        mockMvc.perform(
                MockMvcRequestBuilders.multipart(BASIC_URL)
                        .file("profileImage", createMockProfileImage().getBytes())
                        .param("email", joinRequestDto.getEmail())
                        .param("password", joinRequestDto.getPassword())
                        .param("name", joinRequestDto.getName())
                        .param("mbti", joinRequestDto.getMbti().name())
                        .param("gender", joinRequestDto.getGender())
                        .param("nationality", joinRequestDto.getNationality())
                        .param("birthday", joinRequestDto.getBirthday())
                        .param("selfIntroduction", joinRequestDto.getSelfIntroduction()))
                .andExpect(status().isOk())
                .andDo(document("joinMember",
                        requestParts(
                                partWithName("profileImage").description("프로필 이미지")
                        ),
                        requestParameters(
                                parameterWithName("email").description("이메일"),
                                parameterWithName("password").description("비밀번호"),
                                parameterWithName("name").description("이름"),
                                parameterWithName("mbti").description("MBTI"),
                                parameterWithName("gender").description("성별"),
                                parameterWithName("nationality").description("국적"),
                                parameterWithName("birthday").description("생년월일"),
                                parameterWithName("selfIntroduction").description("자기 소개")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간")
                        )
                ));

    }


    @Test
    @DisplayName("IntegrationController 사용자 정보 조회 - 성공")
    void testGetMemberInfo_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(BASIC_URL)
                        .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                        .header("RefreshToken",tokenDto.getRefreshToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 사용자 정보를 조회하였습니다."))
                .andDo(document("getMemberInfo",
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간"),
                                fieldWithPath("data.memberId").description("회원 아이디"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.mbti").description("mbti"),
                                fieldWithPath("data.gender").description("성별"),
                                fieldWithPath("data.nationality").description("국적"),
                                fieldWithPath("data.birthday").description("생년월일"),
                                fieldWithPath("data.name").description("이름"),
                                fieldWithPath("data.profileImage").description("프로필 이미지"),
                                fieldWithPath("data.selfIntroduction").description("자기 소개"),
                                fieldWithPath("data.age").description("나이")
                        )
                ));

    }


    @Test
    @DisplayName("IntegrationController 회원 탈퇴 - 성공")
    void testMemberWithdrawal_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password));


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
                .andExpect(status().isOk())
                .andDo(document("memberWithdrawal",
                        requestFields(
                                fieldWithPath("password").description("회원 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간")

                        )
                ));
    }


    @Test
    @DisplayName("IntegrationController 회원 이메일 조회 - 성공")
    void testMemberEmail_Success() throws Exception {
        //given
        memberService.register(memberEntity);

        // when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get(BASIC_URL + "/email/{email}/existence", email)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.existence").value(true))
                .andDo(document("isExistMemberEmail",
                        pathParameters(parameterWithName("email").description("이메일")),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간"),
                                fieldWithPath("data.existence").description("이메일 존재 여부")
                        )
                ));
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
                        RestDocumentationRequestBuilders.post(BASIC_URL + "/{email}/password/temp", email)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(nameMap))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("issueTemporaryPassword",
                        pathParameters(parameterWithName("email").description("이메일")),
                        requestFields(
                                fieldWithPath("name").description("이름")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간")
                        )
                ));
    }


    @Test
    @DisplayName("IntegrationController 비밀번호 변경 요청 - 성공")
    void ChangePassword_Success() throws Exception {
        // given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password));

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
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("changePassword",
                        requestFields(
                                fieldWithPath("currentPassword").description("현재 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간")
                        )
                ));
    }
    @Test
    @DisplayName("IntegrationController 프로필 자기소개 변경 요청 - 성공")
    void ChangeSelfIntroduction_Success() throws Exception {
        // given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password));
        String selfIntroductionChangeRequest = "반가워요!";

        // when & then
        mockMvc.perform(
                        put(BASIC_URL + "/self-introduction")
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("selfIntroductionChangeRequest", selfIntroductionChangeRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("changeSelfIntroduction",
                        requestParameters(
                                parameterWithName("selfIntroductionChangeRequest").description("프로필 자기소개")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간")
                        )
                ));
    }


    @Test
    @DisplayName("IntegrationController 프로필 이미지 수정 요청 - 성공")
    void ChangeProfileImage_Success() throws Exception {
        // given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password));

        // When
        mockMvc.perform(
                MockMvcRequestBuilders.multipart(HttpMethod.PUT,BASIC_URL+"/profile-image")
                        .file("profileImage",createMockProfileImage().getBytes())
                        .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                        .header("RefreshToken",tokenDto.getRefreshToken())
                )
                .andExpect(status().isOk())
                .andDo(document("changeProfileImage",
                        requestParts(
                                partWithName("profileImage").description("프로필 이미지")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간")
                        )
                ));

    }

}
