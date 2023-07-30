package com.aliens.friendship.applicant.business;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.applicant.business.ApplicantBusiness;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantRequestDto;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantResponseDto;
import com.aliens.friendship.domain.applicant.controller.dto.PartnersResponseDto;
import com.aliens.friendship.domain.applicant.converter.ApplicantConverter;
import com.aliens.friendship.domain.applicant.service.ApplicantService;
import com.aliens.friendship.domain.applicant.service.MatchingInfoService;
import com.aliens.friendship.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicantBusinessTest {

    @Mock
    private MemberService memberService;

    @Mock
    private ApplicantService applicantService;

    @Mock
    private MatchingInfoService matchingInfoService;

    @Mock
    private ApplicantConverter applicantConverter;

    @InjectMocks
    private ApplicantBusiness applicantBusiness;

    private MemberEntity loginMemberEntity;
    private ApplicantEntity applicantEntity;
    private Instant now;

    @BeforeEach
    public void setup() {
        loginMemberEntity = MemberEntity.builder().status(MemberEntity.Status.APPLIED).build();
        applicantEntity = ApplicantEntity.builder().isMatched(ApplicantEntity.Status.MATCHED).build();
        now = Instant.now();
    }

    @Test
    @DisplayName("매칭 신청 내용 조회 - 성공")
    public void testGetApplicant() throws Exception {
        // when
        when(memberService.getCurrentMemberEntity()).thenReturn(loginMemberEntity);
        when(applicantService.findByMemberEntity(loginMemberEntity)).thenReturn(applicantEntity);
        when(applicantConverter.toApplicantResponseDto(applicantEntity)).thenReturn(ApplicantResponseDto.builder().build());

        // then
        ApplicantResponseDto result = applicantBusiness.getApplicant();
        assertEquals(ApplicantResponseDto.class, result.getClass());
    }

    @Test
    @DisplayName("매칭 신청 - 성공")
    public void testApplyMatching() throws Exception {
        // given
        ApplicantRequestDto applicantRequestDto = new ApplicantRequestDto();

        // when
        when(memberService.getCurrentMemberEntity()).thenReturn(loginMemberEntity);
        when(applicantConverter.toApplicantEntity(loginMemberEntity, applicantRequestDto)).thenReturn(applicantEntity);

        // then
        applicantBusiness.applyMatching(applicantRequestDto);
    }

    @Test
    @DisplayName("매칭 상태 조회 - 성공")
    public void testGetMatchingStatus() throws Exception {
        // when
        when(memberService.getCurrentMemberEntity()).thenReturn(loginMemberEntity);
        when(applicantService.findByMemberEntity(loginMemberEntity)).thenReturn(applicantEntity);

        // then
        Map<String, String> result = applicantBusiness.getMatchingStatus();
        assertEquals(Collections.singletonMap("status", "MATCHED"), result);
    }

    @Test
    @DisplayName("매칭된 파트너들 조회 - 성공")
    public void testGetPartnersResponse() throws Exception {
        // given
        MatchingEntity matchingEntity = MatchingEntity.builder().matchedMember(loginMemberEntity).build();
        List<MatchingEntity> matchingEntities = Collections.singletonList(matchingEntity);
        ApplicantEntity matchedMembersApplicantEntity = ApplicantEntity.builder()
                .memberEntity(loginMemberEntity).firstPreferLanguage(ApplicantEntity.Language.ENGLISH)
                        .secondPreferLanguage(ApplicantEntity.Language.CHINESE).build();


        // when
        when(memberService.getCurrentMemberEntity()).thenReturn(loginMemberEntity);
        when(applicantService.findByMemberEntity(loginMemberEntity)).thenReturn(matchedMembersApplicantEntity);
        when(applicantService.getDateWillMatched(matchedMembersApplicantEntity)).thenReturn(now);
        when(matchingInfoService.getMatchingResultsByDateAndMemberEntity(now, loginMemberEntity))
                .thenReturn(matchingEntities);
        PartnersResponseDto.Partner partnerDto = PartnersResponseDto.Partner.builder().build();
        when(applicantConverter.toPartnerOfResponseDto(matchingEntity, matchedMembersApplicantEntity))
                .thenReturn(partnerDto);


        // then
        PartnersResponseDto result = applicantBusiness.getPartnersResponse();
        assertEquals(Collections.singletonList(partnerDto), result.getPartners());
    }

    @Test
    @DisplayName("매칭 예정 날짜 반환 - 성공")
    public void testGetMatchingDate() throws Exception {
        // when
        when(memberService.getCurrentMemberEntity()).thenReturn(loginMemberEntity);
        when(applicantService.findByMemberEntity(loginMemberEntity)).thenReturn(applicantEntity);
        when(applicantService.getDateWillMatched(applicantEntity)).thenReturn(now);
        when(applicantConverter.getFormattedStringDate(now)).thenReturn("2023-07-25");

        // then
        Map<String, String> result = applicantBusiness.getMatchingDate();
        assertEquals(Collections.singletonMap("matchingCompleteDate", "2023-07-25"), result);
    }
}