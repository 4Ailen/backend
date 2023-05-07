package com.aliens.friendship.matching.service;


import com.aliens.friendship.matching.controller.dto.ApplicantResponse;
import com.aliens.friendship.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.matching.domain.Language;
import com.aliens.friendship.matching.repository.LanguageRepository;
import com.aliens.friendship.matching.domain.Applicant;
import com.aliens.friendship.matching.repository.ApplicantRepository;
import com.aliens.friendship.matching.controller.dto.ApplicantRequest;
import com.aliens.friendship.matching.repository.MatchingRepository;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchingInfoService {

    private final LanguageRepository languageRepository;
    private final MemberRepository memberRepository;
    private final ApplicantRepository applicantRepository;
    private final MatchingRepository matchingRepository;

    public Map<String, Object> getLanguages() {
        return Collections.singletonMap("languages", languageRepository.findAll());
    }

    public void applyMatching(ApplicantRequest applicantRequest) {
        Member member = memberRepository.findById(getCurrentMemberId()).get();
        validateNotApplied(member);

        Applicant applicant = Applicant.builder()
                .member(member)
                .firstPreferLanguage(getLanguageById(applicantRequest.getFirstPreferLanguage()))
                .secondPreferLanguage(getLanguageById(applicantRequest.getSecondPreferLanguage()))
                .isMatched(Applicant.Status.NOT_MATCHED)
                .build();
        member.updateStatus(Member.Status.APPLIED);

        applicantRepository.save(applicant);
    }

    private Language getLanguageById(Integer languageId) {
        return languageRepository.findById(languageId).orElseThrow(() -> new IllegalArgumentException("잘못된 언어값입니다."));
    }

    public Map<String, String> getMatchingStatus() {
        Member member = memberRepository.findById(getCurrentMemberId()).get();
        String status;
        if (member.getStatus().equals(Member.Status.APPLIED)) {
            if (applicantRepository.findById(member.getId()).get().getIsMatched() == Applicant.Status.MATCHED) {
                status = "MATCHED";
            } else {
                status = "PENDING";
            }
        } else {
            status = "NOT_APPLIED";
        }
        return Collections.singletonMap("status", status);
    }

    public PartnersResponse getPartnersResponse() {
        Member member = memberRepository.findById(getCurrentMemberId()).get();
        PartnersResponse partnersResponse = new PartnersResponse();
        for (Member partner : getPartners(member)) {
            if (partner.getStatus() == Member.Status.WITHDRAWN) {
                PartnersResponse.Member partnerDto = PartnersResponse.Member.builder()
                        .memberId(-1)
                        .name("탈퇴한 사용자")
                        .mbti("")
                        .gender("")
                        .nationality("")
                        .countryImage("")
                        .profileImage("")
                        .build();
                partnersResponse.getPartners().add(partnerDto);
            } else {
                PartnersResponse.Member partnerDto = PartnersResponse.Member.builder()
                        .memberId(partner.getId())
                        .name(partner.getName())
                        .mbti(partner.getMbti())
                        .gender(partner.getGender())
                        .nationality(partner.getNationality().getNatinalityText())
                        .countryImage(partner.getNationality().getCountryImageUrl())
                        .profileImage(partner.getProfileImageUrl())
                        .build();
                partnersResponse.getPartners().add(partnerDto);
            }
        }
        return partnersResponse;
    }

    private List<Member> getPartners(Member member) {
        validateApplied(member);
        validateMatched(member);
        List<Integer> partnerIds = getPartnerIdsByApplicant(member);
        return memberRepository.findAllById(partnerIds);
    }

    public ApplicantResponse getApplicant() throws Exception {
        Member member = memberRepository.findById(getCurrentMemberId()).get();
        validateApplied(member);
        Applicant applicant = applicantRepository.findById(getCurrentMemberId()).get();
        ApplicantResponse.Member applicantDto = ApplicantResponse.Member.builder()
                .name(member.getName())
                .gender(member.getGender())
                .mbti(member.getMbti())
                .age(member.getAge())
                .nationality(member.getNationality().getNatinalityText())
                .profileImage(member.getProfileImageUrl())
                .countryImage(member.getNationality().getCountryImageUrl())
                .build();
        ApplicantResponse.PreferLanguages preferLanguagesDto = ApplicantResponse.PreferLanguages.builder()
                .firstPreferLanguage(applicant.getFirstPreferLanguage().getLanguageText())
                .secondPreferLanguage(applicant.getSecondPreferLanguage().getLanguageText())
                .build();
        return ApplicantResponse.builder()
                .member(applicantDto)
                .preferLanguages(preferLanguagesDto)
                .build();
    }

    protected Integer getCurrentMemberId() {
        Member member = memberRepository.findByEmail(getCurrentMemberEmail()).get();
        return member.getId();
    }

    private String getCurrentMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private List<Integer> getPartnerIdsByApplicant(Member member) {
        List<Integer> partnerIds = matchingRepository.findPartnerIdsByApplicantId(member.getId());
        if (partnerIds.isEmpty()) {
            throw new IllegalStateException("매칭된 파트너가 없습니다.");
        }
        return partnerIds;
    }

    private void validateMatched(Member member) {
        if (applicantRepository.findById(member.getId()).get().getIsMatched() == Applicant.Status.NOT_MATCHED) {
            throw new IllegalArgumentException("매칭이 완료되지 않은 사용자입니다.");
        }
    }

    private void validateApplied(Member member) {
        boolean ApplicantPresent = applicantRepository.findById(member.getId()).isPresent();
        if (member.getStatus().equals(Member.Status.NOT_APPLIED) && !ApplicantPresent) {
            throw new IllegalArgumentException("매칭 신청을 하지 않은 사용자입니다.");
        }
        if (member.getStatus().equals(Member.Status.APPLIED) && !ApplicantPresent) {
            throw new IllegalArgumentException("매칭 신청자의 정보가 없습니다.");
        }
    }

    private void validateNotApplied(Member member) {
        if (applicantRepository.findById(member.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 매칭을 신청한 사용자입니다.");
        }
    }
}



