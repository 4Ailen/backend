package com.aliens.friendship.matching.service;

import com.aliens.friendship.matching.repository.BlockingInfoRepository;
import com.aliens.friendship.matching.domain.Language;
import com.aliens.friendship.matching.repository.LanguageRepository;
import com.aliens.friendship.matching.domain.MatchingParticipant;
import com.aliens.friendship.matching.repository.MatchingParticipantRepository;
import com.aliens.friendship.matching.service.model.MatchingParticipantInfo;
import com.aliens.friendship.matching.service.model.MatchedGroup;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.repository.MemberRepository;
import com.aliens.friendship.matching.domain.Question;
import com.aliens.friendship.matching.domain.BlockingInfo;
import com.aliens.friendship.matching.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private List<MatchingParticipant> matchingParticipants; // 신청자
    private List<BlockingInfo> blockingInfos; // 사용자들의 차단 정보
    private List<Language> languages; // 언어 리스트
    private List<Integer> languageIds; // 언어 id 리스트
    private List<MatchingParticipant> ans1, ans2; // 1차 필터링(질문 기반)
    private List<List<MatchingParticipant>> ans1Lg, ans2Lg; // 2차 필터링(언어 기반)
    private List<MatchedGroup> matchedTeams; // 매칭된 팀 리스트
    private List<MatchingParticipant> remainApplicants1, remainApplicants2;
    int ttl = 100;

    private final LanguageRepository languageRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final MatchingParticipantRepository matchingParticipantRepository;
    private final BlockingInfoRepository blockingInfoRepository;

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

    public Question chooseQuestion() {
        Question currentQuestion, nextQuestion;
        List<Question> questions;
        int firstQId, lastQId, idx = 1;

        // 현재 isSelected가 1로 설정된 질문을 가져온 후 1을 0으로 변경
        currentQuestion = questionRepository.findByIsSelected((byte) 1);
        currentQuestion.setIsSelected((byte) 0);
        questionRepository.save(currentQuestion);

        questions = questionRepository.findAll();
        firstQId = questions.stream().min(Comparator.comparingInt(Question::getId)).get().getId();
        lastQId = questions.stream().max(Comparator.comparingInt(Question::getId)).get().getId();

        // 다음 질문의 isSelected 값을 1로 변경
        while (true) {
            if (currentQuestion.getId() + idx > lastQId) {
                nextQuestion = questionRepository.findById(firstQId).get();
            } else {
                nextQuestion = questionRepository.findById(currentQuestion.getId() + idx).get();
            }
            // (다음 질문의 id)가 (현재 질문 id+1)이 아닐수도 있음.
            if (nextQuestion == null) {
                idx++;
            } else {
                break;
            }
        }
        nextQuestion.setIsSelected((byte) 1);
        questionRepository.save(nextQuestion);

        return currentQuestion;
    }

    public void applyMatching(MatchingParticipantInfo applicantInfo) {
        // 신청한 member의 is_applied를 waiting으로 변경
        Member member = memberRepository.findById(applicantInfo.getMemberId()).get();
        member.setIsApplied("apply");
        System.out.println("applicantInfo = " + applicantInfo.getAnswer());

        // matching_applicant에 신청자 정보 저장
        MatchingParticipant matchingParticipant = MatchingParticipant.builder()
                .member(member)
                .questionAnswer(applicantInfo.getAnswer())
                .preferredLanguage(languageRepository.findById(applicantInfo.getLanguage()).get())
                .isMatched((byte) 0)
                .groupId(-1)
                .build();

        matchingParticipantRepository.save(matchingParticipant);
    }

    // member 테이블로부터 매칭 상태 반환
    public String checkStatus() {
        // 해당 유저의 정보를 반환하는 코드 아직 구현 안되어 임시로 member 설정
        Member member = memberRepository.findById(10).get();
        System.out.println("member.getIsApplied() = " + member.getIsApplied());
        if (member.getIsApplied().equals("apply")) {
            if (matchingParticipantRepository.findById(10).get().getIsMatched() == 1) {
                return "matched";
            } else {
                return "waiting";
            }
        } else {
            return "none";
        }
    }

    public List<MatchedGroup> teamBuilding() {
        init();
        loadDatas();

        while (ttl > 0) {
            clearLists();
            Collections.shuffle(matchingParticipants);
            filterQuestion();
            filterLanguage(ans1, 1);
            filterLanguage(ans2, 2);
            remainApplicants1 = makeTeam(ans1Lg);
            remainApplicants2 = makeTeam(ans2Lg);
            if (checkBlockingInfo()) {
                break;
            } else {
                ttl--;
            }
        }
        updateMatchingParticipantStatus();

        return matchedTeams;
    }

    private void init() {
        matchingParticipants = new ArrayList<>();
        blockingInfos = new ArrayList<>();
        ans1 = new ArrayList<>();
        ans2 = new ArrayList<>();
        ans1Lg = new ArrayList<>();
        ans2Lg = new ArrayList<>();
        matchedTeams = new ArrayList<>();
        remainApplicants1 = new ArrayList<>();
        remainApplicants2 = new ArrayList<>();
        languages = new ArrayList<>();
        languageIds = new ArrayList<>();
    }

    private void loadDatas() {
        matchingParticipants = matchingParticipantRepository.findAll();
        blockingInfos = blockingInfoRepository.findAll();
        languages = languageRepository.findAll();
        for (int i = 0; i < languages.size(); i++) {
            ans1Lg.add(new ArrayList<>());
            ans2Lg.add(new ArrayList<>());
            languageIds.add(languages.get(i).getId());
        }
    }

    private void filterQuestion() {
        ans1 = matchingParticipants.stream()
                .filter(ans -> ans.getQuestionAnswer() == 1)
                .collect(Collectors.toList());

        ans2 = matchingParticipants.stream()
                .filter(ans -> ans.getQuestionAnswer() == 2)
                .collect(Collectors.toList());
    }

    // ans에 대해 각 언어로 나누기
    private void filterLanguage(List<MatchingParticipant> ans, int ansNum) {
        for (int i = 0; i < languages.size(); i++) {
            int lgIdx = i;
            List<MatchingParticipant> tmp = ans.stream()
                    .filter(lg -> lg.getPreferredLanguage().getId() == languageIds.get(lgIdx))
                    .collect(Collectors.toList());
            if (ansNum == 1) {
                ans1Lg.get(i).addAll(tmp);
            } else if (ansNum == 2) {
                ans2Lg.get(i).addAll(tmp);
            }
        }
    }

    private List<MatchingParticipant> makeTeam(List<List<MatchingParticipant>> filteredList) {
        List<MatchingParticipant> remainApplicants = new ArrayList<>();

        for (int i = 0; i < languages.size(); i++) {
            // 세명씩 팀 구성
            while (filteredList.get(i).size() >= 3) {
                MatchedGroup team = new MatchedGroup(filteredList.get(i).get(0).getMember().getId(),
                        filteredList.get(i).get(1).getMember().getId(),
                        filteredList.get(i).get(2).getMember().getId());
                for (int j = 0; j < 3; j++) {
                    filteredList.get(i).remove(0);
                }
                matchedTeams.add(team);
            }
            // 남은 신청자들
            for (int j = 0; j < filteredList.get(i).size(); j++) {
                remainApplicants.add(filteredList.get(i).get(j));
            }
        }

        // 남은 신청자들 1차: 3명씩 팀
        while (remainApplicants.size() >= 3) {
            MatchedGroup team = new MatchedGroup(remainApplicants.get(0).getMember().getId(),
                    remainApplicants.get(1).getMember().getId(),
                    remainApplicants.get(2).getMember().getId());
            for (int j = 0; j < 3; j++) {
                remainApplicants.remove(0);
            }
            matchedTeams.add(team);
        }

        // 남은 신청자들 2차: 2명씩 팀
        if (remainApplicants.size() == 2) { // 2명 팀
            MatchedGroup team = new MatchedGroup(remainApplicants.get(0).getMember().getId(),
                    remainApplicants.get(1).getMember().getId(),
                    null);
            for (int j = 0; j < 2; j++) {
                remainApplicants.remove(0);
            }
            // 만들어진 팀 추가
            matchedTeams.add(team);
        } else if (remainApplicants.size() == 1) { // 이전에 만들어진 팀의 3명 + 남은 1명으로 2명, 2명 팀
            int id1, id2, id3, id4, lastIdx = matchedTeams.size() - 1;
            id1 = matchedTeams.get(lastIdx).getMemberId1();
            id2 = matchedTeams.get(lastIdx).getMemberId2();
            id3 = matchedTeams.get(lastIdx).getMemberId3();
            id4 = remainApplicants.get(0).getMember().getId();
            matchedTeams.remove(matchedTeams.size() - 1);
            matchedTeams.add(new MatchedGroup(id1, id2, null));
            matchedTeams.add(new MatchedGroup(id3, id4, null));
            remainApplicants.remove(0);
        }

        return remainApplicants;
    }

    // 매칭된 팀에서 차단한 신청자가 같이 매칭된 경우 발견 시 false 반환
    private boolean checkBlockingInfo() {
        for (int i = 0; i < matchedTeams.size(); i++) {
            for (int j = 0; j < blockingInfos.size(); j++) {
                int blockedMemberId = blockingInfos.get(j).getBlockedMember().getId();
                int blockingMemberId = blockingInfos.get(j).getBlockingMember().getId();
                int memberId1 = matchedTeams.get(i).getMemberId1(), memberId2 = matchedTeams.get(i).getMemberId2(), memberId3 = -1;
                if (matchedTeams.get(i).getMemberId3() != null) {
                    memberId3 = matchedTeams.get(i).getMemberId3();
                }
                boolean isBlockedMember = false, isBlockingMember = false;
                if (memberId1 == blockingMemberId || memberId2 == blockingMemberId || memberId3 == blockingMemberId) {
                    isBlockingMember = true;
                }
                if (memberId1 == blockedMemberId || memberId2 == blockedMemberId || memberId3 == blockedMemberId) {
                    isBlockedMember = true;
                }
                if (isBlockingMember && isBlockedMember) {
                    return false;
                }
            }
        }
        return true;
    }

    // 리스트 내 모든 요소 삭제
    private void clearLists() {
        matchedTeams.clear();
        remainApplicants1.clear();
        remainApplicants2.clear();
        ans1.clear();
        ans2.clear();
        for (int i = 0; i < languages.size(); i++) {
            ans1Lg.get(i).clear();
            ans2Lg.get(i).clear();
        }
    }

    // 매칭 완료 후 matching_participant status 변경
    private void updateMatchingParticipantStatus() {
        for (int i = 0; i < matchingParticipants.size(); i++) {
            MatchingParticipant matchingParticipant = matchingParticipants.get(i);
            matchingParticipant.setIsMatched((byte) 1);
            matchingParticipantRepository.save(matchingParticipant);
        }
    }
}
