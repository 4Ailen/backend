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
import com.aliens.friendship.matching.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchingInfoService {

    private final LanguageRepository languageRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final ApplicantRepository applicantRepository;
    private final MatchingRepository matchingRepository;

    public Map<String, Object> getLanguages() {
        List<Language> languages = languageRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        List<List<String>> languageList = new ArrayList<>();
        for (Language language : languages) {
            List<String> languageContent = new ArrayList<>();
            languageContent.add(language.getLanguageText());
            languageContent.add(language.getId().toString());
            languageList.add(languageContent);
        }
        result.put("languages", languageList);

        return result;
    }

    public void applyMatching(MatchingParticipantInfo matchingParticipantInfo) {
        // 신청한 member의 is_applied를 waiting으로 변경
        Member member = memberRepository.findById(matchingParticipantInfo.getMemberId()).get();
        member.updateIsApplied(Member.Status.APPLIED);
        System.out.println("applicantInfo = " + matchingParticipantInfo.getAnswer());

        Applicant applicant = Applicant.builder()
                .member(member)
                .firstPreferLanguage(languageRepository.findById(matchingParticipantInfo.getFirstPreferLanguage()).get())
                .secondPreferLanguage(languageRepository.findById(matchingParticipantInfo.getSecondPreferLanguage()).get())
                .isMatched(MatchingParticipant.Status.NOT_MATCHED)
                .build();

        matchingParticipantRepository.save(matchingParticipant);
    }

    // member 테이블로부터 매칭 상태 반환
    public String checkStatus() {
        // 해당 유저의 정보를 반환하는 코드 아직 구현 안되어 임시로 member 설정
        Member member = memberRepository.findById(10).get();
        System.out.println("member.getIsApplied() = " + member.getIsApplied());
        if (member.getIsApplied().equals(Member.Status.APPLIED)) {
            if (matchingParticipantRepository.findById(10).get().getIsMatched() == MatchingParticipant.Status.MATCHED) {
                return "MATCHED";
            } else {
                return "PENDING";
            }
        } else {
            return "NONE";
        }
    }
}



