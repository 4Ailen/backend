package com.aliens.friendship.member.controller;

import com.aliens.friendship.global.config.jwt.JwtAuthenticationFilter;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("회원가입 성공")
    void Join_Success() throws Exception {
        // given
        JoinDto joinDto = new JoinDto();
        doNothing().when(memberService).join(joinDto);

        // when & then
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(joinDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("회원가입 성공"));
        verify(memberService, times(1)).join(any(JoinDto.class));
    }

    @Test
    @DisplayName("회원정보 조회 성공")
    void GetMemberInfo_Success() throws Exception {
        // given
        MemberInfoDto expectedMemberInfoDto = MemberInfoDto.builder()
                .memberId(1)
                .email("test@example.com")
                .mbti("ENFP")
                .gender("남성")
                .nationality(1)
                .age(24)
                .birthday("1998-12-31")
                .name("Ryan")
                .build();
        when(memberService.getMemberInfo()).thenReturn(expectedMemberInfoDto);

        // when & then
        mockMvc.perform(get("/member")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.memberId").value(expectedMemberInfoDto.getMemberId()))
                .andExpect(jsonPath("$.response.email").value(expectedMemberInfoDto.getEmail()))
                .andExpect(jsonPath("$.response.mbti").value(expectedMemberInfoDto.getMbti()))
                .andExpect(jsonPath("$.response.gender").value(expectedMemberInfoDto.getGender()))
                .andExpect(jsonPath("$.response.nationality").value(expectedMemberInfoDto.getNationality()))
                .andExpect(jsonPath("$.response.age").value(expectedMemberInfoDto.getAge()))
                .andExpect(jsonPath("$.response.birthday").value(expectedMemberInfoDto.getBirthday()))
                .andExpect(jsonPath("$.response.name").value(expectedMemberInfoDto.getName()));
        verify(memberService, times(1)).getMemberInfo();
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void Withdraw_Success() throws Exception {
        // given
        String password = "test1234";
        doNothing().when(memberService).withdraw(password);

        // when & then
        mockMvc.perform(post("/member/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(password))
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
        mockMvc.perform(get("/member/email/" + email + "/existence"))
                .andExpect(status().isOk())
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
        mockMvc.perform(post("/member/" + email + "/password/temp")
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
        mockMvc.perform(put("/member/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").value("비밀번호 변경 성공"));
        verify(memberService, times(1)).changePassword(any(PasswordUpdateRequestDto.class));
    }
}