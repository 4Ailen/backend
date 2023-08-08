package com.aliens.friendship.chat.controller;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.applicant.business.ApplicantBusiness;
import com.aliens.friendship.domain.applicant.service.ApplicantService;
import com.aliens.friendship.domain.auth.business.AuthBusiness;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.chat.business.ChatBusiness;
import com.aliens.friendship.domain.match.business.MatchBusiness;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@Rollback(value = true)
public class IntegrationChatControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MemberConverter memberConverter;

    @Autowired
    AuthBusiness authBusiness;

    @Autowired
    ChatBusiness chatBusiness;

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
        BASIC_URL = "/api/v1/chat";
        email = "test@example312123.com";
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
    @DisplayName("IntegrationController 채팅 토큰 발급 - 성공")
    void tesChattingTokenGenerate_Success() throws Exception {
        //given
        memberService.register(memberEntity);
        TokenDto tokenDto = authBusiness.login(new LoginRequest(email, password), fcmToken);

        applicantService.register(ApplicantEntity.builder().isMatched(ApplicantEntity.Status.NOT_MATCHED)
                .memberEntity(memberEntity)
                .firstPreferLanguage(ApplicantEntity.Language.ENGLISH)
                .secondPreferLanguage(ApplicantEntity.Language.CHINESE)
                .build());

        //given
        for (int i = 0; i < 10; i++) {
            joinRequestDto =
                    JoinRequestDto.builder()
                            .email(email + i)
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

        matchBusiness.matchingAllApplicant();

        mockMvc.perform(
                        get(BASIC_URL + "/token")
                                .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                                .header("RefreshToken", tokenDto.getRefreshToken())
                )
                .andExpect(status().isOk())
                .andDo(document("generateChattingToken"
                ));
    }
}

