package com.aliens.friendship.member.controller;

import com.aliens.friendship.domain.auth.filter.JwtAuthenticationFilter;
import com.aliens.friendship.domain.auth.service.AuthService;
import com.aliens.friendship.domain.member.controller.MemberController;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthService authService;

    @Value("${spring.domain}")
    private String domainUrl;

    private static final String BASIC_URL = "/api/v1/member";

    @Test
    @DisplayName("회원가입 성공")
    void Join_Success() throws Exception {
        // given
        JoinDto joinDto = new JoinDto();
        doNothing().when(memberService).join(joinDto);

        // When & Then
        mockMvc.perform(
                multipart(BASIC_URL)
                        .file((MockMultipartFile) createMockImage())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(joinDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(status().isOk())
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).join(any(JoinDto.class));
    }

    @Test
    @DisplayName("회원가입 성공 - 프로필 이미지가 없는 경우")
    void Join_Success_WithoutProfileImage() throws Exception {
        // given
        JoinDto joinDto = new JoinDto();
        doNothing().when(memberService).join(joinDto);

        // When & Then
        mockMvc.perform(
                multipart("/api/v1/member")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(joinDto))
                )
                .andExpect(status().isOk());

        verify(memberService, times(1)).join(any(JoinDto.class));
    }

    // Mock 회원 프로필 이미지 생성
    private static MockMultipartFile createMockImage()
            throws IOException {
        final String fileName = "test"; //파일명
        final String contentType = "png"; //파일타입
        final String filePath = "src/test/resources/testImage/"+fileName+"."+contentType; //파일경로
        FileInputStream fileInputStream = new FileInputStream(filePath);
        return new MockMultipartFile(
                "profileImage",
                fileName + "." + contentType,
                "image/png",
                fileInputStream
        );
    }

    @Test
    @DisplayName("회원정보 조회 성공")
    void GetMemberInfo_Success() throws Exception {
        // given
        MemberInfoDto expectedMemberInfoDto = MemberInfoDto.builder()
                .email("test@example.com")
                .mbti(Member.Mbti.ENFP)
                .gender("남성")
                .nationality("South Korea")
                .age(24)
                .birthday("1998-12-31")
                .name("Ryan")
                .selfIntroduction("반가워요")
                .profileImage(domainUrl + System.getProperty("user.dir") + "test")
                .build();
        given(memberService.getMemberInfo())
                .willReturn(expectedMemberInfoDto);

        // When & Then
        mockMvc.perform(get(BASIC_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.memberId").isEmpty())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.mbti").value("ENFP"))
                .andExpect(jsonPath("$.data.gender").value("남성"))
                .andExpect(jsonPath("$.data.nationality").value("South Korea"))
                .andExpect(jsonPath("$.data.birthday").value("1998-12-31"))
                .andExpect(jsonPath("$.data.name").value("Ryan"))
                .andExpect(jsonPath("$.data.profileImage").value("/Users/kmo/project/부경대학교 Friendship!/backendtest"))
                .andExpect(jsonPath("$.data.selfIntroduction").value("반가워요"))
                .andExpect(jsonPath("$.data.age").value(24))
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).getMemberInfo();
    }

    @Test
    @DisplayName("프로필 이름과 mbti 값 변경 요청 성공")
    void ChangeProfileNameAndMbti_Success() throws Exception {
        // given
        Map<String, Member.Mbti> mbti = new HashMap<>();
        mbti.put("mbti", Member.Mbti.ISFJ);
        doNothing().when(memberService).changeProfileNameAndMbti(mbti.get("mbti"));

        // when & then
        mockMvc.perform(
                        patch(BASIC_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(mbti))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).changeProfileNameAndMbti(any(Member.Mbti.class));
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void Withdraw_Success() throws Exception {
        // given
        String password = "test1234";
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put("password", password);
        doNothing().when(memberService).withdraw(passwordMap.get("password"));

        // When & Then
        mockMvc.perform(
                        delete(BASIC_URL + "/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(passwordMap))
                                .header("Authorization", "Bearer " + accessToken)
                                .header("RefreshToken", refreshToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).withdraw(password);
    }

    @Test
    @DisplayName("이메일 중복 여부 확인 성공")
    void IsJoinedEmail_Success() throws Exception {
        // given
        String email = "test@case.com";
        given(memberService.isJoinedEmail(email))
                .willReturn(true);

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/email/{email}/existence", email)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.existence").value(true))
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).isJoinedEmail(email);
    }

    @Test
    @DisplayName("회원 임시 비밀번호 발급 요청 성공")
    void IssueTemporaryPassword_Success() throws Exception {
        // given
        String email = "test@case.com";
        String name = "test";
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("name", name);
        doNothing().when(memberService).issueTemporaryPassword(email, nameMap.get("name"));

        // when & then
        mockMvc.perform(
                        post(BASIC_URL + "/{email}/password/temp", email)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(nameMap))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).issueTemporaryPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 요청 성공")
    void ChangePassword_Success() throws Exception {
        // given
        PasswordUpdateRequestDto passwordUpdateRequestDto = PasswordUpdateRequestDto.builder()
                .currentPassword("currentPassword")
                .newPassword("newPassword").build();
        doNothing().when(memberService).changePassword(passwordUpdateRequestDto);

        // when & then
        mockMvc.perform(
                        put(BASIC_URL + "/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(passwordUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).changePassword(any(PasswordUpdateRequestDto.class));
    }


    @Test
    @DisplayName("프로필 자기소개 변경 요청 성공")
    void ChangeSelfIntroduction_Success() throws Exception {
        // given
        String selfIntroductionChangeRequest = "반가워요!";
        doNothing().when(memberService).changeSelfIntroduction(selfIntroductionChangeRequest);

        // when & then
        mockMvc.perform(
                        put(BASIC_URL + "/self-introduction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(selfIntroductionChangeRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).changeSelfIntroduction(any(String.class));
    }

    @Test
    @DisplayName("프로필 이미지 수정 요청 성공")
    void ChangeProfileImage_Success() throws Exception {
        // given
        MockMultipartFile newProfileImage = new MockMultipartFile("profileImage", "test.jpg", "image/jpeg", "test data".getBytes());
        doNothing().when(memberService).changeProfileImage(newProfileImage);

        // when & then
        mockMvc.perform(
                        multipart(PUT, BASIC_URL + "/profile-image")
                                .file(newProfileImage)
                                .contentType("multipart/form-data")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).changeProfileImage(any(MultipartFile.class));
    }

    @DisplayName("프로필 이미지 검증 실패 - 유효하지 않은 크기의 이미지인 경우")
    @Test
    void profileImage_validation_invalid_size() throws Exception {
        // Given
        final String fileName = "bigsize-image"; //파일명
        final String contentType = "png"; //파일타입
        final String filePath = "src/test/resources/testImage/"+fileName+"."+contentType; //파일경로
        FileInputStream fileInputStream = new FileInputStream(filePath);
        MockMultipartFile newProfileImage = new MockMultipartFile("profileImage", "bigsize-image.jpg", "image/png", fileInputStream);

        // When & Then
        mockMvc.perform(
                        multipart(PUT, BASIC_URL + "/profile-image")
                                .file(newProfileImage)
                                .contentType("multipart/form-data"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("GB-C-001"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 요청 파라미터입니다."))
                .andExpect(jsonPath("$.errors[0].field").value("profileImage"))
                .andExpect(jsonPath("$.errors[0].value").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].reason").value("회원 프로필 이미지는 10MB 이하여야 합니다."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @DisplayName("프로필 이미지 검증 실패 - 유효하지 않은 이미지 확장자인 경우")
    @Test
    void profileImage_validation_invalid_extension() throws Exception {
        MockMultipartFile newProfileImage = new MockMultipartFile("profileImage", "test-image.gif", "image/gif", "test-data".getBytes());

        // When & Then
        mockMvc.perform(
                        multipart(PUT, BASIC_URL + "/profile-image")
                                .file(newProfileImage)
                                .contentType("multipart/form-data"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("GB-C-001"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 요청 파라미터입니다."))
                .andExpect(jsonPath("$.errors[0].field").value("profileImage"))
                .andExpect(jsonPath("$.errors[0].value").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].reason").value("회원 프로필 이미지는 [jpg, jpeg, png] 확장자만 가능합니다."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("인증 상태 요청 성공")
    void GetMemberAuthenticationStatus_Success() throws Exception {
        // given
        String email = "test@case.com";
        given(memberService.getMemberAuthenticationStatus(email))
                .willReturn("AUTHENTICATED");

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/{email}/authentication-status", email)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.status").value("AUTHENTICATED"))
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).getMemberAuthenticationStatus(anyString());
    }

    @Test
    @DisplayName("멤버 관련 정보 삭제 성공")
    void deleteMemberInfoByAdmin_Success() throws Exception {
        // given
        Integer memberId = 17;
        doNothing().when(memberService).deleteMemberInfoByAdmin(memberId);

        // when & then
        mockMvc.perform(
                    delete(BASIC_URL + "/{memberId}", memberId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("member/postJoin",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(memberService, times(1)).deleteMemberInfoByAdmin(anyInt());
    }
}