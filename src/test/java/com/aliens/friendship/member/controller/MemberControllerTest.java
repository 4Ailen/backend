package com.aliens.friendship.member.controller;

import com.aliens.friendship.global.config.jwt.JwtAuthenticationFilter;
import com.aliens.friendship.jwt.util.JwtTokenUtil;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Value("${spring.domain}")
    private String domainUrl;

    @Test
    @DisplayName("회원가입 성공")
    void Join_Success() throws Exception {
        // given
        JoinDto joinDto = new JoinDto();
        doNothing().when(memberService).join(joinDto);

        // when & then
        mockMvc.perform(multipart("/api/v1/member")
                        .file((MockMultipartFile) createMockImage())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(joinDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("회원가입 성공"));
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
                .mbti("ENFP")
                .gender("남성")
                .nationality("South Korea")
                .age(24)
                .birthday("1998-12-31")
                .name("Ryan")
                .profileImage(domainUrl + System.getProperty("user.dir") + "test")
                .build();
        when(memberService.getMemberInfo()).thenReturn(expectedMemberInfoDto);

        // when & then
        mockMvc.perform(get("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.email").value(expectedMemberInfoDto.getEmail()))
                .andExpect(jsonPath("$.response.mbti").value(expectedMemberInfoDto.getMbti()))
                .andExpect(jsonPath("$.response.gender").value(expectedMemberInfoDto.getGender()))
                .andExpect(jsonPath("$.response.nationality").value(expectedMemberInfoDto.getNationality()))
                .andExpect(jsonPath("$.response.age").value(expectedMemberInfoDto.getAge()))
                .andExpect(jsonPath("$.response.birthday").value(expectedMemberInfoDto.getBirthday()))
                .andExpect(jsonPath("$.response.name").value(expectedMemberInfoDto.getName()))
                .andExpect(jsonPath("$.response.profileImage").value(expectedMemberInfoDto.getProfileImage()));
        verify(memberService, times(1)).getMemberInfo();
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void Withdraw_Success() throws Exception {
        // given
        String password = "test1234";
        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put("password", password);
        doNothing().when(memberService).withdraw(passwordMap.get("password"));

        // when & then
        mockMvc.perform(post("/api/v1/member/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("회원탈퇴 성공"));
        verify(memberService, times(1)).withdraw(password);
    }

    @Test
    @DisplayName("이메일 중복 여부 확인 성공")
    void IsJoinedEmail_Success() throws Exception {
        // given
        String email = "test@case.com";
        when(memberService.isJoinedEmail(email)).thenReturn(true);

        // when & then
        mockMvc.perform(get("/api/v1/member/email/" + email + "/existence"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.existence").value("true"));
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
        mockMvc.perform(post("/api/v1/member/" + email + "/password/temp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(nameMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("임시 비밀번호 발급 성공"));
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
        mockMvc.perform(put("/api/v1/member/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("비밀번호 변경 성공"));
        verify(memberService, times(1)).changePassword(any(PasswordUpdateRequestDto.class));
    }

    @Test
    @DisplayName("프로필 이름과 mbti 값 변경 요청 성공")
    void ChangeProfileNameAndMbti_Success() throws Exception {
        // given
        Map<String, String> nameAndMbti = new HashMap<>();
        nameAndMbti.put("name", "test");
        nameAndMbti.put("mbti", "ISFJ");
        doNothing().when(memberService).changeProfileNameAndMbti(nameAndMbti.get("name"), nameAndMbti.get("mbti"));

        // when & then
        mockMvc.perform(patch("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(nameAndMbti)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("프로필 이름과 mbti 값 변경 성공"));
        verify(memberService, times(1)).changeProfileNameAndMbti(anyString(), anyString());
    }

    @Test
    @DisplayName("프로필 이미지 수정 요청 성공")
    void ChangeProfileImage_Success() throws Exception {
        // given
        MockMultipartFile newProfileImage = new MockMultipartFile("profileImage", "test.jpg", "image/jpeg", "test data".getBytes());
        doNothing().when(memberService).changeProfileImage(newProfileImage);

        // when & then
        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/member/profile-image")
                        .file(newProfileImage)
                        .contentType("multipart/form-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("프로필 이미지 수정 성공"));
        verify(memberService, times(1)).changeProfileImage(any(MultipartFile.class));
    }

    @Test
    @DisplayName("인증 상태 요청 성공")
    void GetMemberAuthenticationStatus_Success() throws Exception {
        // given
        String email = "test@case.com";
        when(memberService.getMemberAuthenticationStatus(email)).thenReturn("AUTHENTICATED");

        // when & then
        mockMvc.perform(get("/api/v1/member/" + email + "/authentication-status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.status").value("AUTHENTICATED"));
        verify(memberService, times(1)).getMemberAuthenticationStatus(anyString());
    }

}