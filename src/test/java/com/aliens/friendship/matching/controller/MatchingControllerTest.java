package com.aliens.friendship.matching.controller;

import com.aliens.friendship.domain.auth.filter.JwtAuthenticationFilter;
import com.aliens.friendship.domain.chatting.service.ChattingService;
import com.aliens.friendship.domain.matching.controller.MatchingController;
import com.aliens.friendship.domain.matching.controller.dto.ReportRequest;
import com.aliens.friendship.domain.matching.service.ReportService;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantRequest;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantResponse;
import com.aliens.friendship.domain.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.domain.matching.domain.Language;
import com.aliens.friendship.domain.matching.service.BlockingInfoService;
import com.aliens.friendship.domain.matching.service.MatchingInfoService;
import com.aliens.friendship.domain.matching.service.MatchingService;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import com.aliens.friendship.domain.member.repository.NationalityRepository;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.response.ResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MatchingController.class)
class MatchingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MatchingInfoService matchingInfoService;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    MatchingService matchingService;

    @MockBean
    ChattingService chattingService;

    @MockBean
    BlockingInfoService blockingInfoService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private NationalityRepository nationalityRepository;

    @MockBean
    private ReportService reportService;

    @MockBean
    private ResponseService responseService;

    @Test
    @DisplayName("언어 목록 조회 성공")
    void GetLanguages_Success() throws Exception {
        // Given
        Map<String, Object> languageResponse = new HashMap<>();
        List<Language> languages = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            languages.add(Language.builder()
                    .id(i + 1)
                    .languageText("language" + (i + 1))
                    .build());
        }
        languageResponse.put("languages", languages);
        when(matchingInfoService.getLanguages()).thenReturn(languageResponse);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/matching/languages"));

        // Then
        verify(matchingInfoService, times(1)).getLanguages();
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("매칭 참가 신청 성공")
    void ApplyMatching_Success() throws Exception {
        // given
        ApplicantRequest request = new ApplicantRequest();
        request.setFirstPreferLanguage(1);
        request.setSecondPreferLanguage(2);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/matching/applicant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        verify(matchingInfoService, times(1)).applyMatching(any());
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("매칭 상태 조회 성공")
    void GetMatchingStatus_Success() throws Exception {
        // Given
        Map<String, String> status = new HashMap<>();
        status.put("status", "MATCHED");
        when(matchingInfoService.getMatchingStatus()).thenReturn(status);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/matching/status"));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("파트너 목록 조회 성공")
    void GetPartners_Success() throws Exception {
        // Given
        PartnersResponse partnersResponse = new PartnersResponse();
        List<PartnersResponse.Member> partners = PartnersFixture.createPartners(4);
        partnersResponse.setPartners(partners);
        when(matchingInfoService.getPartnersResponse()).thenReturn(partnersResponse);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/matching/partners"));

        // Then
        resultActions.andExpect(status().isOk());

        for (int i = 0; i < partners.size(); i++) {
            PartnersResponse.Member partnerDto = partners.get(i);
            resultActions.andExpect(jsonPath("$.data.partners[" + i + "].memberId").value(partnerDto.getMemberId()))
                    .andExpect(jsonPath("$.data.partners[" + i + "].name").value(partnerDto.getName()));
        }

        verify(matchingInfoService).getPartnersResponse();
    }


    @Test
    @DisplayName("신청 정보 조회 성공")
    void GetApplicant_Success() throws Exception {
        // Given
        ApplicantResponse.Member applicantDto = ApplicantResponse.Member.builder()
                .name("Ryan")
                .gender("MALE")
                .mbti(Member.Mbti.INTJ)
                .age(25)
                .nationality("American")
                .profileImage("Profile_URL")
                .countryImage("Korea_URL")
                .build();
        ApplicantResponse.PreferLanguages preferLanguagesDto = ApplicantResponse.PreferLanguages.builder()
                .firstPreferLanguage("English")
                .secondPreferLanguage("Spanish")
                .build();
        ApplicantResponse returnDto = ApplicantResponse.builder()
                .member(applicantDto)
                .preferLanguages(preferLanguagesDto)
                .build();
        when(matchingInfoService.getApplicant()).thenReturn(returnDto);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/matching/applicant"));

        // Then
        resultActions.andExpect(status().isOk());

        verify(matchingInfoService, times(1)).getApplicant();
    }

    @Test
    @DisplayName("차단 성공")
    void Blocking_Success() throws Exception {

        // given
        Integer memberId = 1;
        Long roomId = 1L;

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/matching/partner/{memberId}/block", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomId.toString()));
        // then
        resultActions.andExpect(status().isOk());
        verify(chattingService, times(1)).blockChattingRoom(roomId);
        verify(blockingInfoService, times(1)).block(memberId);
    }

    @Test
    @DisplayName("신고 성공")
    void Report_Success() throws Exception {
        //given
        String ReportCategory = "VIOLENCE";
        String ReportContent = "테스트 신고 내용입니다.";
        Integer memberId = 1;

        Map<String, String> reportRequest = new HashMap<>();
        reportRequest.put("reportCategory", ReportCategory);
        reportRequest.put("reportContent", ReportContent);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/matching/partner/{memberId}/report", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(reportRequest)));

        // then
        resultActions.andExpect(status().isOk());
        verify(reportService, times(1)).report(any(Integer.class), any(ReportRequest.class));
    }

    @Test
    @DisplayName("매칭 남은 기간 요청 성공")
    void GetMatchingRemainingPeriod_Success() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/matching/remaining-period"));

        // then
        resultActions.andExpect(status().isOk());
        verify(matchingInfoService, times(1)).getMatchingRemainingPeroid();
    }
}

