package com.aliens.friendship.domain.applicant.business;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantRequestDto;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantResponseDto;
import com.aliens.friendship.domain.applicant.controller.dto.PartnersResponseDto;
import com.aliens.friendship.domain.applicant.converter.ApplicantConverter;
import com.aliens.friendship.domain.applicant.service.ApplicantService;
import com.aliens.friendship.domain.applicant.service.MatchingInfoService;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.common.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Business
@Slf4j
public class ApplicantBusiness {

    private final MemberService memberService;
    private  final ApplicantService applicantService;
    private final MatchingInfoService matchingInfoService;
    private final ApplicantConverter applicantConverter;


    /**
     * 매칭 신청 내용 조회
     */
    public ApplicantResponseDto getApplicant() throws Exception {
        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        // 탈퇴하지 않은 회원 검증
        matchingInfoService.validateApplied(loginMemberEntity);

        //최근 참여기록 검색
        ApplicantEntity applicantEntity = applicantService.getApplicantByMemberEntity(loginMemberEntity);

        // Dto 변환
        ApplicantResponseDto applicantResponseDto = applicantConverter.toApplicantResponseDto(applicantEntity);

        return applicantResponseDto;
    }

    /**
     * 매칭 신청
     */
    public void applyMatching(ApplicantRequestDto applicantRequestDto) throws Exception {
        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        // 탈퇴하지 않은 회원 검증
        matchingInfoService.validateApplied(loginMemberEntity);

        //Dto -> Entity
        ApplicantEntity applicantEntity = applicantConverter.toApplicantEntity(loginMemberEntity,applicantRequestDto);

        //저장
        applicantService.register(applicantEntity);
    }

    /**
     * 매칭 상태 조회
     */
    public Map<String, String> getMatchingStatus() throws Exception {
        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity = memberService.getCurrentMemberEntity();

        // 신청자 엔티티
        Optional<ApplicantEntity> applicantEntity = applicantService.findApplicantByMemberEntity(loginMemberEntity);

        //상태
        String status;

        if (applicantEntity.isPresent()) {
            if (applicantEntity.get().getIsMatched() == ApplicantEntity.Status.MATCHED) {
                status = "MATCHED";
            } else {
                status = "PENDING";
            }
        } else {
            status = "NOT_APPLIED";
        }

        //상태 반환
        return Collections.singletonMap("status", status);
    }


    /**
     * 매칭된 파트너들 조회
     */
    public PartnersResponseDto getPartnersResponse() throws Exception {
        //로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        //최근 신청엔티티
        ApplicantEntity applicantEntity = applicantService.getApplicantByMemberEntity(loginMemberEntity);

        //신청에 따른 매칭될 날짜
        Instant matchedDate = applicantService.getDateWillMatched(applicantEntity);

        //매칭 정보
        List<MatchingEntity> matchingEntities =  matchingInfoService.getMatchingResultsByDateAndMemberEntity(matchedDate,loginMemberEntity);

        // 결과 객체로 반환
        PartnersResponseDto resultDto = new PartnersResponseDto();

        for (MatchingEntity matching : matchingEntities ) {
            MemberEntity partner = matching.getMatchedMember();

            if (partner.getStatus() == MemberEntity.Status.WITHDRAWN) {
                PartnersResponseDto.Partner partnerDto = applicantConverter.getWithdrawalMember();
                resultDto.getPartners().add(partnerDto);
            }
            else{
                ApplicantEntity matchedMembersApplicantEntity = applicantService.getApplicantByMemberEntity(matching.getMatchedMember());
                PartnersResponseDto.Partner partnerDto =  applicantConverter.toPartnerOfResponseDto(matching,matchedMembersApplicantEntity);
                resultDto.getPartners().add(partnerDto);
            }
        }
        return resultDto;

    }


    /**
     * 매칭 예정 날짜 반환
     */
    public Map<String,String> getMatchingDate() throws Exception {
        //로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        //최근 신청엔티티
        ApplicantEntity applicantEntity = applicantService.getApplicantByMemberEntity(loginMemberEntity);

        //신청에 따른 매칭될 날짜
        Instant matchedDate = applicantService.getDateWillMatched(applicantEntity);

        //Instant -> formatting "2023-07-25"
        String dateString = applicantConverter.getFormattedStringDate(matchedDate);

        //전달
        Map<String, String> result = new HashMap<>();
        result.put("matchingCompleteDate", dateString);
        return result;
    }

}
