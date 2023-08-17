package com.aliens.friendship.admin;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.auth.entity.AuthorityEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.db.report.ReportCategory;
import com.aliens.db.report.entity.ReportEntity;
import com.aliens.db.report.repository.ReportRepository;
import com.aliens.friendship.domain.applicant.service.ApplicantService;
import com.aliens.friendship.domain.auth.business.AuthBusiness;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.member.controller.dto.JoinRequestDto;
import com.aliens.friendship.domain.member.converter.MemberConverter;
import com.aliens.friendship.domain.member.service.MemberService;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class IntegrationAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MemberConverter memberConverter;

    @Autowired
    MemberService memberService;

    @Autowired
    AuthBusiness authBusiness;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ApplicantService applicantService;

    @Autowired
    MemberRepository memberRepository;

    String BASIC_URL;
    String adminEmail;
    String memberEmail;
    String password;
    String fcmToken;
    JoinRequestDto joinRequestDto;
    MemberEntity adminEntity;
    MemberEntity memberEntity;

    @BeforeEach
    public void setUp() {
        //given
        BASIC_URL = "/admin";
        adminEmail = "admin@example.com";
        memberEmail = "member@example.com";
        password = "test1234";
        fcmToken = "testFcmToken";

        joinRequestDto =
                JoinRequestDto.builder()
                        .email(adminEmail)
                        .password(password)
                        .name("Aden")
                        .mbti(MemberEntity.Mbti.INTJ)
                        .gender("Male")
                        .nationality("USA")
                        .birthday("1990-01-01")
                        .selfIntroduction("Hello, I am Aden.")
                        .profileImage(createMockProfileImage())
                        .build();

        adminEntity = memberConverter.toMemberEntityWithUser(joinRequestDto);
        adminEntity.addAuthority(AuthorityEntity.ofAdmin(adminEntity));

        joinRequestDto =
                JoinRequestDto.builder()
                        .email(memberEmail)
                        .password(password)
                        .name("Joy")
                        .mbti(MemberEntity.Mbti.ISFJ)
                        .gender("Female")
                        .nationality("Swiss")
                        .birthday("1993-07-19")
                        .selfIntroduction("Hello, I am Joy.")
                        .profileImage(createMockProfileImage())
                        .build();
        memberEntity = memberConverter.toMemberEntityWithUser(joinRequestDto);
        memberService.register(memberEntity);
    }


    private MockMultipartFile createMockProfileImage() {
        return new MockMultipartFile("profileImage", "profile.png", "image/png", new byte[0]);
    }


    @Test
    @DisplayName("IntegrationController 회원 정보 삭제 - 성공")
    void testDeleteMemberInfoByAdmin_Success() throws Exception {
        //given
        memberService.register(adminEntity);
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(adminEmail, password), fcmToken);

        // when & then
        mockMvc.perform(
                        delete(BASIC_URL + "/{memberId}", memberEntity.getId())
                                .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                                .header("RefreshToken", tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 관련 정보 삭제에 성공하였습니다."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("deleteMemberInfoByAdmin",
                        pathParameters(parameterWithName("memberId").description("삭제할 멤버 아이디")),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간")
                        )
                ));
    }

    @Test
    @DisplayName("IntegrationController 회원 정보 조회 - 성공")
    void testGetMemberInfoByAdmin_Success() throws Exception {
        //given
        memberService.register(adminEntity);
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(adminEmail, password), fcmToken);

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/{email}", memberEntity.getEmail())
                                .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                                .header("RefreshToken", tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 사용자 정보를 조회하였습니다."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("getMemberInfoByAdmin",
                        pathParameters(parameterWithName("email").description("조회할 멤버 아이디")),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.mbti").description("MBTI"),
                                fieldWithPath("data.gender").description("성별"),
                                fieldWithPath("data.nationality").description("국적"),
                                fieldWithPath("data.joinDate").description("회원 가입 일자"),
                                fieldWithPath("data.status").description("회원 상태"),
                                fieldWithPath("data.name").description("이름"),
                                fieldWithPath("data.withdrawalAt").description("탈퇴 일자"),
                                fieldWithPath("data.birthday").description("생년월일"),
                                fieldWithPath("data.age").description("나이")
                        )
                ));
    }

    @Test
    @DisplayName("IntegrationController 신고 목록 조회 - 성공")
    void testGetReportsByAdmin_Success() throws Exception {
        //given
        memberService.register(adminEntity);
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(adminEmail, password), fcmToken);

        joinRequestDto =
                JoinRequestDto.builder()
                        .email(memberEmail + "2")
                        .password(password)
                        .name("Joy")
                        .mbti(MemberEntity.Mbti.ISFJ)
                        .gender("Female")
                        .nationality("Swiss")
                        .birthday("1993-07-19")
                        .selfIntroduction("Hello, I am Joy.")
                        .profileImage(createMockProfileImage())
                        .build();
        MemberEntity memberEntity2 = memberConverter.toMemberEntityWithUser(joinRequestDto);
        memberService.register(memberEntity2);

        for (int i = 0; i < 3; i++) {
            ReportEntity reportEntity = ReportEntity.builder()
                    .reportedMemberEntity(memberEntity)
                    .reportingMemberEntity(memberEntity2)
                    .category(ReportCategory.SPAM)
                    .content("spam" + "i")
                    .build();
            reportRepository.save(reportEntity);
        }

        // when & then
        mockMvc.perform(
                        get(BASIC_URL + "/report")
                                .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                                .header("RefreshToken", tokenDto.getRefreshToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 신고 목록을 조회하였습니다."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("getReportsByAdmin",
                        responseFields(
                                fieldWithPath("message").description("성공 메시지"),
                                fieldWithPath("timestamp").description("처리 시간"),
                                fieldWithPath("data.reports[].reportId").description("신고 ID"),
                                fieldWithPath("data.reports[].reportedMemberId").description("신고 대상 회원 ID"),
                                fieldWithPath("data.reports[].reporterMemberId").description("신고자 회원 ID"),
                                fieldWithPath("data.reports[].reportCategory").description("신고 카테고리"),
                                fieldWithPath("data.reports[].reportContent").description("신고 내용"),
                                fieldWithPath("data.reports[].reportDate").description("신고 일자")
                        )
                ));
    }

    @Test
    @DisplayName("IntegrationController 매칭 로직 실행 - 성공")
    void testMatchingPartners_Success() throws Exception {
        //given
        memberService.register(adminEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(adminEmail, password), fcmToken);

        //given
        for (int i = 0; i < 10; i++) {
            joinRequestDto =
                    JoinRequestDto.builder()
                            .email(memberEmail + i)
                            .password(password)
                            .name("Aden" + i)
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

        mockMvc.perform(
                        post(BASIC_URL + "/match")
                                .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                                .header("RefreshToken", tokenDto.getRefreshToken())
                )
                .andExpect(status().isOk())
                .andDo(document("matching"
                ));
    }

}
