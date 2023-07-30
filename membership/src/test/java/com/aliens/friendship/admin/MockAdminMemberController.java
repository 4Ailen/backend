//package com.aliens.friendship.admin;
//
//
//import com.aliens.db.member.entity.MemberEntity;
//import com.aliens.friendship.domain.auth.business.AuthBusiness;
//import com.aliens.friendship.domain.emailauthentication.business.EmailAuthenticationBusiness;
//import com.aliens.friendship.domain.emailauthentication.service.EmailAuthenticationService;
//import com.aliens.friendship.domain.member.business.MemberBusiness;
//import com.aliens.friendship.domain.member.controller.MemberController;
//import com.aliens.friendship.domain.member.controller.dto.MemberInfoByAdminResponseDto;
//import com.aliens.friendship.domain.member.converter.MemberConverter;
//import com.aliens.friendship.domain.member.service.MemberService;
//import com.aliens.friendship.domain.member.service.ProfileImageService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.Instant;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
////@AutoConfigureRestDocs
//
//@AutoConfigureMockMvc(addFilters = false)
//@WebMvcTest(MemberController.class)
//class MockAdminMemberController {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MemberService memberService;
//
//    @MockBean
//    private MemberConverter memberConverter;
//
//    @MockBean
//    private EmailAuthenticationService emailAuthenticationService;
//
//    @MockBean
//    private MemberBusiness memberBusiness;
//
//    @MockBean
//    private ProfileImageService profileImageService;
//
//    @MockBean
//    private AuthBusiness authBusiness;
//
//    @MockBean
//    private EmailAuthenticationBusiness emailAuthenticationBusiness;
//
//
//
//    @Test
//    @DisplayName("멤버 관련 정보 삭제 - 성공")
//    void DeleteMemberInfoByAdmin_Success() throws Exception {
//        // given
//        Long memberId = Long.valueOf(17);
//        doNothing().when(memberService).deleteMemberInfoByAdmin(memberId);
//
//        // when & then
//        mockMvc.perform(
//                        delete("/admin" + "/{memberId}", memberId)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").exists())
//                .andExpect(jsonPath("$.timestamp").exists());
////                .andDo(print());
////                .andDo(document("member/postJoin",
////                        preprocessRequest(prettyPrint()),
////                        preprocessResponse(prettyPrint())
////                ));
//
//        verify(memberService, times(1)).deleteMemberInfoByAdmin(anyLong());
//    }
//
//    @Test
//    @DisplayName("관리자에 의한 멤버 정보 조회 - 성공")
//    void GetMemberInfoByAdmin_Success() throws Exception {
//        // given
//        String email = "test@example.com";
//        MemberInfoByAdminResponseDto expectedMemberInfoByAdminResponseDto = MemberInfoByAdminResponseDto.builder()
//                .email("test@example.com")
//                .mbti(MemberEntity.Mbti.ENFP)
//                .gender("여성")
//                .nationality("South Korea")
//                .age(24)
//                .birthday("2002-07-18")
//                .name("Joy")
//                .withdrawalAt("2023-05-29")
//                .joinDate(Instant.now())
//                .status(MemberEntity.Status.APPLIED)
//                .build();
//        when(memberBusiness.getMemberInfoByAdmin(email)).thenReturn(expectedMemberInfoByAdminResponseDto);
//
//        // when & then
//        mockMvc.perform(get("/admin" + email)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//        verify(memberBusiness, times(1)).getMemberInfoByAdmin(anyString());
//    }
//
//}