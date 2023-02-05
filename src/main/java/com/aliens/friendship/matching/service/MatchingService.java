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
    int ttl;

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

    public void applyMatching(MatchingParticipantInfo matchingParticipantInfo) {
        // 신청한 member의 is_applied를 waiting으로 변경
        Member member = memberRepository.findById(matchingParticipantInfo.getMemberId()).get();
        member.updateIsApplied(Member.Status.APPLIED);
        System.out.println("applicantInfo = " + matchingParticipantInfo.getAnswer());

        // matching_applicant에 신청자 정보 저장
        MatchingParticipant matchingParticipant = MatchingParticipant.builder()
                .member(member)
                .questionAnswer(matchingParticipantInfo.getAnswer())
                .preferredLanguage(languageRepository.findById(matchingParticipantInfo.getLanguage()).get())
                .isMatched(MatchingParticipant.Status.NOT_MATCHED)
                .groupId(-1)
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
            makeRemainApplicantsTeam(remainApplicants1, remainApplicants2);
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
        ttl=100;
    }

    private void loadDatas() {
        matchingParticipants = matchingParticipantRepository.findAll();
        blockingInfos = blockingInfoRepository.findAll();
        languages = languageRepository.findAll();
        for (Language language : languages) {
            ans1Lg.add(new ArrayList<>());
            ans2Lg.add(new ArrayList<>());
            languageIds.add(language.getId());
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
        List<MatchedGroup> ansLgMatchedTeams = new ArrayList<>();

        for (int i = 0; i < languages.size(); i++) {
            // 본인 포함 4명씩 팀 구성
            while (filteredList.get(i).size() >= 4) {
                MatchedGroup team = new MatchedGroup(filteredList.get(i).get(0).getMember().getId(),
                        filteredList.get(i).get(1).getMember().getId(),
                        filteredList.get(i).get(2).getMember().getId(),
                        filteredList.get(i).get(3).getMember().getId(),
                        null);
                filteredList.get(i).subList(0, 4).clear();
                ansLgMatchedTeams.add(team);
            }
            // 남은 신청자들
            remainApplicants.addAll(filteredList.get(i));
        }

        // 남은 신청자들 1차: 4명씩 팀
        while (remainApplicants.size() >= 4) {
            MatchedGroup team = new MatchedGroup(remainApplicants.get(0).getMember().getId(),
                    remainApplicants.get(1).getMember().getId(),
                    remainApplicants.get(2).getMember().getId(),
                    remainApplicants.get(3).getMember().getId(),
                    null);
            remainApplicants.subList(0, 4).clear();
            ansLgMatchedTeams.add(team);
        }

        // 남은 신청자들 2차: 만들어진 각 팀에 한 명씩 추가
        if (remainApplicants.size() > 0) {
            int ansLgMatchedTeamsIdx = 0;
            while (ansLgMatchedTeamsIdx < ansLgMatchedTeams.size()) {
                ansLgMatchedTeams.get(ansLgMatchedTeamsIdx).setMemberId5(remainApplicants.get(0).getMember().getId());
                remainApplicants.remove(0);
                ansLgMatchedTeamsIdx++;
                if (remainApplicants.size() <= 0) break;
            }
        }

        // 남은 신청자들 3차
        if (remainApplicants.size() == 3) { // 3명이 남은 경우: 3명 팀
            ansLgMatchedTeams.add(new MatchedGroup(remainApplicants.get(0).getId(),
                    remainApplicants.get(1).getId(),
                    remainApplicants.get(2).getId(),
                    null,
                    null));
            remainApplicants.clear();
        } else if (remainApplicants.size() == 2) { // 2명이 남은 경우: 마지막 팀(5명)에 2명을 더하여 (3명, 4명) 팀으로 재구성
            if (ansLgMatchedTeams.size() > 0) {
                ansLgMatchedTeams = makeOneTeamToTwoTeam(ansLgMatchedTeams, remainApplicants);
                remainApplicants.clear();
            }
        } else if (remainApplicants.size() == 1) { // 1명이 남은 경우: 마지막 팀(5명)에 1명을 더하여 (3명, 3명) 팀으로 재구성
            if (ansLgMatchedTeams.size() > 0) {
                ansLgMatchedTeams = makeOneTeamToTwoTeam(ansLgMatchedTeams, remainApplicants);
                remainApplicants.clear();
            }
        }

        // 만들어진 팀들을 matchedTeams에 추가
        matchedTeams.addAll(ansLgMatchedTeams);

        return remainApplicants;
    }

    private void makeRemainApplicantsTeam(List<MatchingParticipant> remainApplicants1, List<MatchingParticipant> remainApplicants2) {
        List<MatchingParticipant> remainApplicants = new ArrayList<>();
        remainApplicants.addAll(remainApplicants1);
        remainApplicants.addAll(remainApplicants2);
        // remainApplicants1과 remainApplicants2의 인원의 범위는 각각 0~2명
        if (remainApplicants.size() == 4) { // 남은 신청자가 4명인 경우: 한 팀 생성
            MatchedGroup matchedTeam = new MatchedGroup(remainApplicants.get(0).getId(),
                    remainApplicants.get(1).getId(),
                    remainApplicants.get(2).getId(),
                    remainApplicants.get(3).getId(),
                    null);
            matchedTeams.add(matchedTeam);
            remainApplicants.clear();
        } else if (remainApplicants.size() == 3) { // 남은 신청자가 3명인 경우: 한 팀 생성
            matchedTeams.add(new MatchedGroup(remainApplicants.get(0).getId(),
                    remainApplicants.get(1).getId(),
                    remainApplicants.get(2).getId(),
                    null,
                    null));
            remainApplicants.clear();
        } else if (remainApplicants.size() == 2) { // 남은 신청자가 2명인 경우: 마지막 팀이 4명이면 2명 더해서 (3명, 3명)으로, 5명이면 2명 더해서 (4명, 3명)으로 팀 재구성
            matchedTeams = makeOneTeamToTwoTeam(matchedTeams, remainApplicants);
            remainApplicants.clear();
        } else if (remainApplicants.size() == 1) { // 마지막 팀이 4명이면 +1 해서 5명, 마지막 팀이 5명이면 +1해서 3, 3으로
            matchedTeams = makeOneTeamToTwoTeam(matchedTeams, remainApplicants);
            remainApplicants.clear();
        }
    }

    // 매칭된 팀에서 차단한 신청자가 같이 매칭된 경우 발견 시 false 반환
    private boolean checkBlockingInfo() {
        for (MatchedGroup matchedTeam : matchedTeams) {
            for (BlockingInfo blockingInfo : blockingInfos) {
                int blockedMemberId = blockingInfo.getBlockedMember().getId();
                int blockingMemberId = blockingInfo.getBlockingMember().getId();
                int memberId1 = matchedTeam.getMemberId1(),
                        memberId2 = matchedTeam.getMemberId2(),
                        memberId3 = matchedTeam.getMemberId3(),
                        memberId4 = -1, memberId5 = -1;
                if (matchedTeam.getMemberId4() != null) {
                    memberId4 = matchedTeam.getMemberId4();
                }
                if (matchedTeam.getMemberId5() != null) {
                    memberId5 = matchedTeam.getMemberId5();
                }
                boolean isBlockedMember = false, isBlockingMember = false;
                if (memberId1 == blockingMemberId || memberId2 == blockingMemberId || memberId3 == blockingMemberId || memberId4 == blockingMemberId || memberId5 == blockingMemberId) {
                    isBlockingMember = true;
                }
                if (memberId1 == blockedMemberId || memberId2 == blockedMemberId || memberId3 == blockedMemberId || memberId4 == blockedMemberId || memberId5 == blockedMemberId) {
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
        for (MatchingParticipant matchingParticipant : matchingParticipants) {
            matchingParticipant.updateIsMatched(MatchingParticipant.Status.MATCHED);
            matchingParticipantRepository.save(matchingParticipant);
        }
    }

    private List<MatchedGroup> makeOneTeamToTwoTeam(List<MatchedGroup> teams, List<MatchingParticipant> remainApplicants) {
        Integer id1, id2, id3, id4, id5, id6 = null, id7 = null, lastIdx = teams.size() - 1;
        id1 = teams.get(lastIdx).getMemberId1();
        id2 = teams.get(lastIdx).getMemberId2();
        id3 = teams.get(lastIdx).getMemberId3();
        id4 = teams.get(lastIdx).getMemberId4();
        if (teams.get(lastIdx).getMemberId5() != null) {
            id5 = teams.get(lastIdx).getMemberId4();
            id6 = remainApplicants.get(0).getMember().getId();
            if (remainApplicants.size() == 2) {
                id7 = remainApplicants.get(1).getMember().getId();
            }
        } else {
            id5 = remainApplicants.get(0).getMember().getId();
            if (remainApplicants.size() == 2) {
                id6 = remainApplicants.get(1).getMember().getId();
            }
        }
        teams.remove(teams.size() - 1);
        if (id6 == null && id7 == null) {
            teams.add(new MatchedGroup(id1, id2, id3, id4, id5));
        } else {
            teams.add(new MatchedGroup(id1, id2, id3, null, null));
            teams.add(new MatchedGroup(id4, id5, id6, id7, null));
        }

        return teams;
    }
}
