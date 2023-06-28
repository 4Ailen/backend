package com.aliens.friendship.domain.matching.service;


import com.aliens.friendship.domain.matching.controller.dto.ApplicantResponse;
import com.aliens.friendship.domain.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.domain.matching.domain.Language;
import com.aliens.friendship.domain.matching.exception.ApplicantNotFoundException;
import com.aliens.friendship.domain.matching.exception.DuplicatedMatchException;
import com.aliens.friendship.domain.matching.exception.LanguageNotFoundException;
import com.aliens.friendship.domain.matching.exception.MatchingNotFoundException;
import com.aliens.friendship.domain.matching.repository.LanguageRepository;
import com.aliens.friendship.domain.matching.domain.Applicant;
import com.aliens.friendship.domain.matching.repository.ApplicantRepository;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantRequest;
import com.aliens.friendship.domain.matching.repository.MatchingRepository;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.aliens.friendship.domain.matching.exception.MatchingExceptionCode.*;

@Service
@RequiredArgsConstructor
public class MatchingInfoService {

    private final LanguageRepository languageRepository;
    private final MemberRepository memberRepository;
    private final ApplicantRepository applicantRepository;
    private final MatchingRepository matchingRepository;

    @Value("${file-server.domain}")
    private String domainUrl;

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
        return languageRepository.findById(languageId).orElseThrow(LanguageNotFoundException::new);
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
                        .mbti(null)
                        .gender("")
                        .nationality("")
                        .firstPreferLanguage("")
                        .secondPreferLanguage("")
                        .profileImage("")
                        .build();
                partnersResponse.getPartners().add(partnerDto);
            } else {
                Applicant applicant = applicantRepository.findById(partner.getId()).get();
                PartnersResponse.Member partnerDto = PartnersResponse.Member.builder()
                        .memberId(partner.getId())
                        .name(partner.getName())
                        .mbti(partner.getMbti())
                        .gender(partner.getGender())
                        .nationality(partner.getNationality())
                        .firstPreferLanguage(applicant.getFirstPreferLanguage().getLanguageText())
                        .secondPreferLanguage(applicant.getSecondPreferLanguage().getLanguageText())
                        .profileImage(domainUrl + partner.getProfileImageUrl())
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
                .nationality(member.getNationality())
                .profileImage(member.getProfileImageUrl())
                .countryImage(member.getNationality())
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
            throw new MatchingNotFoundException(MATCHED_PARTNER_NOT_FOUND);
        }
        return partnerIds;
    }

    private void validateMatched(Member member) {
        if (applicantRepository.findById(member.getId()).get().getIsMatched() == Applicant.Status.NOT_MATCHED) {
            throw new ApplicantNotFoundException(MATCH_NOT_COMPLETED);
        }
    }

    private void validateApplied(Member member) {
        boolean ApplicantPresent = applicantRepository.findById(member.getId()).isPresent();
        if (member.getStatus().equals(Member.Status.NOT_APPLIED) && !ApplicantPresent) {
            throw new ApplicantNotFoundException(MATCH_REQUEST_NOT_SUBMITTED);
        }
        if (member.getStatus().equals(Member.Status.APPLIED) && !ApplicantPresent) {
            throw new ApplicantNotFoundException();
        }
    }

    private void validateNotApplied(Member member) {
        if (applicantRepository.findById(member.getId()).isPresent()) {
            throw new DuplicatedMatchException();
        }
    }
}