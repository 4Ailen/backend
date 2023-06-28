package com.aliens.friendship.matching.service;

import com.aliens.friendship.domain.matching.controller.dto.ApplicantResponse;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantRequest;
import com.aliens.friendship.domain.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.domain.matching.domain.Applicant;
import com.aliens.friendship.domain.matching.domain.Language;
import com.aliens.friendship.domain.matching.exception.LanguageNotFoundException;
import com.aliens.friendship.domain.matching.repository.ApplicantRepository;
import com.aliens.friendship.domain.matching.repository.LanguageRepository;
import com.aliens.friendship.domain.matching.repository.MatchingRepository;
import com.aliens.friendship.domain.matching.service.MatchingInfoService;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingInfoServiceTest {

    @InjectMocks
    private MatchingInfoService matchingInfoService;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicantRepository applicantRepository;

    @Mock
    private MatchingRepository matchingRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("언어 목록 조회 성공")
    void GetLanguages_Success() {
        // given
        Language language1 = new Language(0, "Korean");
        Language language2 = new Language(1, "Chinese");
        Language language3 = new Language(2, "Japanese");
        List<Language> expectedLanguages = Arrays.asList(language1, language2, language3);
        when(languageRepository.findAll()).thenReturn(expectedLanguages);

        // when
        Map<String, Object> result = matchingInfoService.getLanguages();

        // then
        assertEquals(expectedLanguages, result.get("languages"));
    }

    @Test
    @DisplayName("매칭 신청 성공")
    void ApplyMatching_Success() {

        // given
        Member member = MemberFixture.createTestMember(Member.Status.NOT_APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        Language firstPreferLanguage = new Language(1, "English");
        Language secondPreferLanguage = new Language(2, "Spanish");
        when(languageRepository.findById(firstPreferLanguage.getId())).thenReturn(Optional.of(firstPreferLanguage));
        when(languageRepository.findById(secondPreferLanguage.getId())).thenReturn(Optional.of(secondPreferLanguage));
        ApplicantRequest applicantRequest = new ApplicantRequest();
        applicantRequest.setFirstPreferLanguage(firstPreferLanguage.getId());
        applicantRequest.setSecondPreferLanguage(secondPreferLanguage.getId());

        // when
        matchingInfoService.applyMatching(applicantRequest);

        // then
        assertEquals(Member.Status.APPLIED, member.getStatus());
        verify(applicantRepository, times(1)).save(any(Applicant.class));
    }

    @Test
    @DisplayName("매칭 신청 실패 - 이미 신청한 상태")
    void ApplyMatching_ThrowException_When_GivenAppliedMember() {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        when(applicantRepository.findById(any())).thenReturn(Optional.of(new Applicant()));

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            matchingInfoService.applyMatching(new ApplicantRequest());
        });

        // then
        assertEquals("이미 매칭을 신청한 사용자입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("매칭 신청 실패 - 잘못된 언어값 제공")
    void ApplyMatching_ThrowException_When_GivenInvalidLanguage() {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.NOT_APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        Language firstPreferLanguage = new Language(1, "English");
        Language secondPreferLanguage = new Language(342, "WrongLanguage");
        when(languageRepository.findById(firstPreferLanguage.getId())).thenReturn(Optional.of(firstPreferLanguage));
        when(languageRepository.findById(secondPreferLanguage.getId())).thenReturn(Optional.empty());
        ApplicantRequest applicantRequest = new ApplicantRequest();
        applicantRequest.setFirstPreferLanguage(firstPreferLanguage.getId());
        applicantRequest.setSecondPreferLanguage(secondPreferLanguage.getId());

        // when
        Exception exception = assertThrows(LanguageNotFoundException.class, () -> {
            matchingInfoService.applyMatching(applicantRequest);
        });

        // then
        assertEquals("존재하지 않는 언어입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("매칭 상태 조회 성공")
    void GetMatchingStatus_Success() {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        Applicant applicant = mock(Applicant.class);
        when(applicantRepository.findById(anyInt())).thenReturn(Optional.of(applicant));
        when(applicant.getIsMatched()).thenReturn(Applicant.Status.MATCHED);

        // when
        Map<String, String> status = matchingInfoService.getMatchingStatus();

        // then
        assertEquals("MATCHED", status.get("status"));
    }

    @Test
    @DisplayName("파트너 목록 조회 성공")
    void GetPartners_Success() {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        Applicant applicant = ApplicantFixture.createTestApplicant(member);
        applicant.updateIsMatched(Applicant.Status.MATCHED);
        when(applicantRepository.findById(anyInt())).thenReturn(Optional.of(applicant));
        Member partner1 = MemberFixture.createTestMember(1, "test1@example.com");
        Member partner2 = MemberFixture.createTestMember(2, "test2@example.com");
        Member partner3 = MemberFixture.createTestMember(3, "test3@example.com");
        List<Integer> partnerIds = Arrays.asList(partner1.getId(), partner2.getId(), partner3.getId());
        List<Member> partners = Arrays.asList(partner1, partner2, partner3);
        when(matchingRepository.findPartnerIdsByApplicantId(anyInt())).thenReturn(partnerIds);
        when(memberRepository.findAllById(partnerIds)).thenReturn(partners);
        System.out.println(partner1.getProfileImageUrl() + "123");
        when(applicantRepository.findById(anyInt())).thenReturn(Optional.of(applicant));

        // when
        PartnersResponse partnersResponse = matchingInfoService.getPartnersResponse();

        // then
        assertAll(
            () -> assertEquals(3, partnersResponse.getPartners().size()),
            () -> {
                for (int i = 0; i < partners.size(); i++) {
                    PartnersResponse.Member partnerResponse = partnersResponse.getPartners().get(i);
                    Member partner = partners.get(i);
                    assertEquals(partner.getId(), partnerResponse.getMemberId());
                    assertEquals(partner.getName(), partnerResponse.getName());
                    assertEquals(partner.getMbti(), partnerResponse.getMbti());
                    assertEquals(partner.getGender(), partnerResponse.getGender());
                    assertEquals(partner.getNationality(), partnerResponse.getNationality());
//                    assertEquals(partner.getProfileImageUrl(), partnerResponse.getProfileImage());
                }
            }
        );

    }

    @Test
    @DisplayName("파트너 목록 조회 실패 - 신청이 되지 않은 상태")
    void GetPartners_ThrowException_When_GivenNotAppliedMember() {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.NOT_APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        when(applicantRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            matchingInfoService.getPartnersResponse();
        });

        // then
        assertEquals("매칭 신청을 하지 않은 사용자입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("파트너 목록 조회 실패 - 신청은 되어 있지만 신청자 정보가 없는 상태")
    void GetPartners_ThrowException_When_NoSuchApplicant() {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        when(applicantRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            matchingInfoService.getPartnersResponse();
        });

        // then
        assertEquals("매칭 신청자의 정보가 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("파트너 목록 조회 실패 - 매칭이 되지 않은 상태")
    void GetPartners_ThrowException_When_GivenNotMatchedMember() {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        Applicant applicant = mock(Applicant.class);
        when(applicant.getIsMatched()).thenReturn(Applicant.Status.NOT_MATCHED);
        when(applicantRepository.findById(anyInt())).thenReturn(Optional.of(applicant));
        when(applicantRepository.findById(anyInt())).thenReturn(Optional.of(applicant));

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            matchingInfoService.getPartnersResponse();
        });

        // then
        assertEquals("매칭이 완료되지 않은 사용자입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("파트너 목록 조회 실패 - 매칭된 파트너가 없는 상태")
    void GetPartners_ThrowException_When_GivenMatchedMemberWithNoPartner() {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        Applicant applicant = mock(Applicant.class);
        when(applicant.getIsMatched()).thenReturn(Applicant.Status.MATCHED);
        when(applicantRepository.findById(anyInt())).thenReturn(Optional.of(applicant));
        when(matchingRepository.findPartnerIdsByApplicantId(anyInt())).thenReturn(Collections.emptyList());

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            matchingInfoService.getPartnersResponse();
        });

        // then
        assertEquals("매칭된 파트너가 없습니다.", exception.getMessage());
    }



    @Test
    @DisplayName("신청자 정보 조회 성공")
    void GetApplicant_Success() throws Exception {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        Applicant applicant = ApplicantFixture.createTestApplicant(member);
        when(applicantRepository.findById(applicant.getId())).thenReturn(Optional.of(applicant));

        // when
        ApplicantResponse applicantResponse = matchingInfoService.getApplicant();

        // then
        assertAll(
            () -> assertNotNull(applicantResponse),
            () -> {
                assertEquals(member.getName(), applicantResponse.getMember().getName());
                assertEquals(member.getGender(), applicantResponse.getMember().getGender());
                assertEquals(member.getMbti(), applicantResponse.getMember().getMbti());
                assertEquals(member.getNationality(), applicantResponse.getMember().getNationality());
                assertEquals(member.getAge(), applicantResponse.getMember().getAge());
                assertEquals(member.getNationality(), applicantResponse.getMember().getCountryImage());
                assertEquals(member.getProfileImageUrl(), applicantResponse.getMember().getProfileImage());
                assertEquals(applicant.getFirstPreferLanguage().getLanguageText(), applicantResponse.getPreferLanguages().getFirstPreferLanguage());
                assertEquals(applicant.getSecondPreferLanguage().getLanguageText(), applicantResponse.getPreferLanguages().getSecondPreferLanguage());
            }
        );
    }

    @Test
    @DisplayName("신청자 정보 조회 실패 - 신청이 되지 않은 상태")
    void GetApplicant_When_GivenNotAppliedMember() throws Exception {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.NOT_APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        when(applicantRepository.findById(member.getId())).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            matchingInfoService.getApplicant();
        });

        // then
        assertEquals("매칭 신청을 하지 않은 사용자입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("신청자 정보 조회 실패 - 신청은 되어 있지만 신청자 정보가 없는 상태")
    void GetApplicant_When_NoSuchApplicant() throws Exception {
        // given
        Member member = MemberFixture.createTestMember(Member.Status.APPLIED);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        setUpAuthentication(member);
        when(applicantRepository.findById(member.getId())).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            matchingInfoService.getApplicant();
        });

        // then
        assertEquals("매칭 신청자의 정보가 없습니다.", exception.getMessage());
    }

    private void setUpAuthentication(Member member) {
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
    }
}
