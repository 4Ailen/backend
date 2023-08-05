package com.aliens.friendship.applicant.controller;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.applicant.business.ApplicantBusiness;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantRequestDto;
import com.aliens.friendship.domain.applicant.service.ApplicantService;
import com.aliens.friendship.domain.auth.business.AuthBusiness;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.match.business.MatchBusiness;
import com.aliens.friendship.domain.member.controller.dto.JoinRequestDto;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationApplicationControllerTest {

    @Autowired
    private MemberRepository memberRepository;

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
    ApplicantBusiness applicantBusiness;

    @Autowired
    ApplicantService applicantService;

    @Autowired
    MatchBusiness matchBusiness;

    String BASIC_URL;
    String email;
    String password;
    String fcmToken;
    JoinRequestDto joinRequestDto;
    MemberEntity memberEntity;

    @BeforeEach
    public void setUp() {
        //given
        BASIC_URL = "/api/v1/applicant";
        email = "test@example.com";
        password = "test1234";
        fcmToken = "testFcmToken";

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
    @DisplayName("IntegrationController 매칭 신청 - 성공")
    void testApplicant_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email, password), fcmToken);


        ApplicantRequestDto applicantRequestDto = ApplicantRequestDto.builder()
                .firstPreferLanguage("ENGLISH")
                .secondPreferLanguage("KOREAN")
                .build();


        mockMvc.perform(
                        post(BASIC_URL)
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(applicantRequestDto)
                                )
                )
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("IntegrationController 본인 매칭 신청 조회 - 성공")
    void testGetMyApplicant_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password),fcmToken);

        applicantService.register(ApplicantEntity.builder().isMatched(ApplicantEntity.Status.NOT_MATCHED)
                .memberEntity(memberEntity)
                .firstPreferLanguage(ApplicantEntity.Language.ENGLISH)
                .secondPreferLanguage(ApplicantEntity.Language.CHINESE)
                .build());

        mockMvc.perform(
                        get(BASIC_URL)
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                                )
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("IntegrationController 매칭 상태 조회 - 성공")
    void testGetMyApplicantStatus_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password),fcmToken);

        applicantService.register(ApplicantEntity.builder().isMatched(ApplicantEntity.Status.NOT_MATCHED)
                .memberEntity(memberEntity)
                .firstPreferLanguage(ApplicantEntity.Language.ENGLISH)
                .secondPreferLanguage(ApplicantEntity.Language.CHINESE)
                .build());

        mockMvc.perform(
                        get(BASIC_URL+"/status")
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                )
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("IntegrationController 매칭 일시 조회 - 성공")
    void testMatchingTime_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password),fcmToken);

        applicantService.register(ApplicantEntity.builder().isMatched(ApplicantEntity.Status.NOT_MATCHED)
                .memberEntity(memberEntity)
                .firstPreferLanguage(ApplicantEntity.Language.ENGLISH)
                .secondPreferLanguage(ApplicantEntity.Language.CHINESE)
                .build());

        mockMvc.perform(
        get(BASIC_URL+"/completion-date")
                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                .header("RefreshToken",tokenDto.getRefreshToken())
                )
                .andExpect(status().isOk());
    }


    //MUST SEE:화,토요일 적용안됌(시간에 따른 조회이기 때문)
    @Test
    @DisplayName("IntegrationController 매칭 파트너 조회 - 성공")
    void testMatchingPartners_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password),fcmToken);

        applicantService.register(ApplicantEntity.builder().isMatched(ApplicantEntity.Status.NOT_MATCHED)
                .memberEntity(memberEntity)
                .firstPreferLanguage(ApplicantEntity.Language.ENGLISH)
                .secondPreferLanguage(ApplicantEntity.Language.CHINESE)
                .build());

        //given
        for(int i = 0; i < 10; i++){
            joinRequestDto =
                    JoinRequestDto.builder()
                            .email(email+i)
                            .password(password)
                            .name("Aden"+i)
                            .mbti(MemberEntity.Mbti.INTJ)
                            .gender("Male")
                            .nationality("USA")
                            .birthday("1990-01-01")
                            .selfIntroduction("Hello, I am Aden.")
                            .profileImage(createMockProfileImage())
                            .build();
            memberEntity = memberRepository.save(memberConverter.toMemberEntityWithUser(joinRequestDto));
            memberService.register(memberEntity);

            applicantService.register(ApplicantEntity.builder().isMatched(ApplicantEntity.Status.NOT_MATCHED)
                    .memberEntity(memberEntity)
                    .firstPreferLanguage(ApplicantEntity.Language.ENGLISH)
                    .secondPreferLanguage(ApplicantEntity.Language.CHINESE)
                    .build());
        }

        matchBusiness.matchingAllApplicant();

        mockMvc.perform(
                        get(BASIC_URL+"/partners")
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("IntegrationController 매칭 선호 언어 수정 - 성공")
    void testChangePreferLanguages_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email,password));

        applicantService.register(ApplicantEntity.builder().isMatched(ApplicantEntity.Status.NOT_MATCHED)
                .memberEntity(memberEntity)
                .firstPreferLanguage(ApplicantEntity.Language.ENGLISH)
                .secondPreferLanguage(ApplicantEntity.Language.CHINESE)
                .build());

        ApplicantRequestDto modifiedPreferLanguage = ApplicantRequestDto.builder()
                .firstPreferLanguage("ENGLISH")
                .secondPreferLanguage("KOREAN")
                .build();

        mockMvc.perform(
                        patch(BASIC_URL+"/prefer-languages")
                                .header("Authorization", "Bearer "+ tokenDto.getAccessToken())
                                .header("RefreshToken",tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(modifiedPreferLanguage)
                                )
                )
                .andExpect(status().isOk());

    }

}
