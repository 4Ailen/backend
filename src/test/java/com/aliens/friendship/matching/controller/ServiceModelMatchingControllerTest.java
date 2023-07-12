package com.aliens.friendship.matching.controller;

import com.aliens.friendship.domain.auth.filter.JwtAuthenticationFilter;
import com.aliens.friendship.domain.chatting.service.ChattingService;
import com.aliens.friendship.domain.matching.controller.MatchingController;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantRequest;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantResponse;
import com.aliens.friendship.domain.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.domain.matching.controller.dto.ReportRequest;
import com.aliens.friendship.domain.matching.domain.Language;
import com.aliens.friendship.domain.matching.service.BlockingInfoService;
import com.aliens.friendship.domain.matching.service.MatchingInfoService;
import com.aliens.friendship.domain.matching.service.MatchingService;
import com.aliens.friendship.domain.matching.service.ReportService;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import com.aliens.friendship.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@WebMvcTest(MatchingController.class)
class ServiceModelMatchingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    private ReportService reportService;

    private static final String BASIC_URL = "/api/v1/matching";

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
        given(matchingInfoService.getLanguages())
                .willReturn(languageResponse);

        // When & then
        mockMvc.perform(
                        get(BASIC_URL + "/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.languages[0].id").value(1))
                .andExpect(jsonPath("$.data.languages[0].languageText").value("language1"))
                .andDo(print());
//                .andDo(document("matching/getLanguages",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(matchingInfoService, times(1)).getLanguages();
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
        given(matchingInfoService.getApplicant())
                .willReturn(returnDto);

        // When & Then
        mockMvc.perform(
                        get(BASIC_URL + "/applicant")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.member.name").value("Ryan"))
                .andExpect(jsonPath("$.data.member.gender").value("MALE"))
                .andExpect(jsonPath("$.data.member.mbti").value("INTJ"))
                .andExpect(jsonPath("$.data.member.nationality").value("American"))
                .andExpect(jsonPath("$.data.member.age").value(25))
                .andExpect(jsonPath("$.data.member.profileImage").value("Profile_URL"))
                .andExpect(jsonPath("$.data.member.countryImage").value("Korea_URL"))
                .andExpect(jsonPath("$.data.preferLanguages.firstPreferLanguage").value("English"))
                .andExpect(jsonPath("$.data.preferLanguages.secondPreferLanguage").value("Spanish"))
                .andDo(print());
//                .andDo(document("matching/getApplicant",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(matchingInfoService, times(1)).getApplicant();
    }

    @Test
    @DisplayName("매칭 참가 신청 성공")
    void ApplyMatching_Success() throws Exception {
        // given
        ApplicantRequest request = new ApplicantRequest();
        request.setFirstPreferLanguage(1);
        request.setSecondPreferLanguage(2);

        // When & Then
        mockMvc.perform(
                        post(BASIC_URL + "/applicant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("matching/postApplicant",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        // then
//        verify(matchingInfoService, times(1)).applyMatching(any());
    }

    @Test
    @DisplayName("매칭 상태 조회 성공")
    void GetMatchingStatus_Success() throws Exception {
        // Given
        Map<String, String> status = new HashMap<>();
        status.put("status", "MATCHED");
        given(matchingInfoService.getMatchingStatus())
                .willReturn(status);

        // When & Then
        mockMvc.perform(
                        get(BASIC_URL + "/status")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.status").value("MATCHED"))
                .andDo(print());
//                .andDo(document("matching/getStatus",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));
    }

    @Test
    @DisplayName("파트너 목록 조회 성공")
    void GetPartners_Success() throws Exception {
        // Given
        PartnersResponse partnersResponse = new PartnersResponse();
        List<PartnersResponse.Member> partners = PartnersFixture.createPartners(4);
        partnersResponse.setPartners(partners);
        given(matchingInfoService.getPartnersResponse())
                .willReturn(partnersResponse);

        // When & Then
        mockMvc.perform(
                        get(BASIC_URL + "/partners")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.partners[0].memberId").value(1))
                .andExpect(jsonPath("$.data.partners[0].name").value("Partner1"))
                .andExpect(jsonPath("$.data.partners[0].mbti").value("INTJ"))
                .andExpect(jsonPath("$.data.partners[0].gender").value("GENDER1"))
                .andExpect(jsonPath("$.data.partners[0].nationality").value("Nationality1"))
                .andExpect(jsonPath("$.data.partners[0].profileImage").value("ProfileImageURL1"))
                .andExpect(jsonPath("$.data.partners[0].firstPreferLanguage").value("Korean"))
                .andExpect(jsonPath("$.data.partners[0].secondPreferLanguage").value("Chinese"))
                .andExpect(jsonPath("$.data.partners[0].selfIntroduction").isEmpty())
                .andDo(print());
//                .andDo(document("matching/getPartners",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(matchingInfoService).getPartnersResponse();
    }


    @Test
    @DisplayName("차단 성공")
    void Blocking_Success() throws Exception {

        // given
        Integer memberId = 1;
        Long roomId = 1L;

        // When & Then
        mockMvc.perform(
                        post(BASIC_URL + "/partner/{memberId}/block", memberId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(roomId.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("matching/postBlcok",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

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

        // When & Then
        mockMvc.perform(
                        post(BASIC_URL + "/partner/{memberId}/report", memberId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(reportRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
//                .andDo(document("matching/postReport",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint())
//                ));

        verify(reportService, times(1)).report(any(Integer.class), any(ReportRequest.class));
    }

    @Test
    @DisplayName("매칭 남은 기간 요청 성공")
    void GetMatchingRemainingPeriod_Success() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/matching/remaining-period"));

        // then
        resultActions.andExpect(status().isOk());
        verify(matchingInfoService, times(1)).getMatchingRemainingPeroid();
    }
}