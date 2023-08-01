package com.aliens.friendship.domain.match.business;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.friendship.domain.applicant.service.ApplicantService;
import com.aliens.friendship.domain.match.converter.MatchConverter;
import com.aliens.friendship.domain.match.service.model.Participant;
import com.aliens.friendship.domain.match.service.model.ServiceModelMatching;
import com.aliens.friendship.global.common.annotation.Business;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Business
@RequiredArgsConstructor
public class MatchBusiness {
    private final MatchConverter matchConverter;
    private final MatchSaveBusiness matchSaveBusiness;
    private final ApplicantService applicantService;
    private static final int MAX_TRIES = 200;
    private Map<String, Queue<Participant>> languageQueuesWithCandidates = new HashMap<>();
    private int maxMatches = 3;
    private final List<String> languages =  List.of("KOREAN","ENGLISH","JAPANESE","CHINESE");
    private List<Participant> matchingParticipants;

    public void matchingAllApplicant() throws Exception {

        //신청자 Entities
        List<ApplicantEntity> applicantEntities = applicantService.findAllParticipants();

        //ApplicantEntity -> Participant 매칭에 사용되는 객채로 변환
        matchingParticipants = matchConverter.toParticipants(applicantEntities);

        //선호언호 별 Queue 에  Participant 추가
        languageQueuesWithCandidates = createNewLanguageQueuesWithCandidates();

        //제 1 선호 언어 Queue 를 통한 최대 3명 매칭
        matchParticipantsWithCandidatesWhoHasFirstPreferLanguages();

        //제 1 선호 언어 Queue 를 통해 모두 3명 매칭이 안되었을 경우
        //제 2 선호 언어 Queue 를 통해 남은 매칭을 진행
        if (!isAllMatched()) {matchParticipantsWithCandidatesWhoHasSecondPreferLanguages();}

        //제 1,2 선호 언어 Queue 를 통해 매칭을 시도했지만,
        // 아직 3명의 매칭이 안된 인원들에 대해서 랜덤 매칭
        if (!isAllMatched()) {matchParticipantsWhoHasLessThanThreeMatchesWithCandidatesWhoHasLessThanFourMatches();} //랜덤

        // 신청자는 두 종류로 나눠진다
        // ( 3명 매칭된 신청자, 선호언어가 맞지않아 3명 미만으로 매칭된 신청자)
        // 두 종류의 신청자를 랜덤 매칭하여 1인당 최소 3명, 최대 4명의 매칭이 되도록한다.
        matchParticipantsWithSpecialFriends();

        // 매칭 저장
        matchSaveBusiness.saveMatchingResult(matchingParticipants);
    }


    public void matchParticipantsWithCandidatesWhoHasFirstPreferLanguages() {
        for (Participant participant : matchingParticipants) {
            Queue<Participant> candidates = languageQueuesWithCandidates.get(participant.getFirstPreferLanguage());
            matchParticipantWithCandidates(participant, candidates);
        }
    }

    public void matchParticipantsWithCandidatesWhoHasSecondPreferLanguages() {
        List<Participant> participants = getParticipantsWithLessThanNumberOfMatches(3);
        languageQueuesWithCandidates = createNewLanguageQueuesWithCandidates();
        for (Participant participant : participants) {
            Queue<Participant> candidates = languageQueuesWithCandidates.get(participant.getSecondPreferLanguage());
            matchParticipantWithCandidates(participant, candidates);
        }
        maxMatches++;
    }

    public void matchParticipantsWhoHasLessThanThreeMatchesWithCandidatesWhoHasLessThanFourMatches() {
        List<Participant> participants = getParticipantsWithLessThanNumberOfMatches(3);
        Queue<Participant> candidates = new LinkedList<>(getParticipantsWithLessThanNumberOfMatches(4));
        for (Participant participant : participants) {
            matchParticipantWithCandidates(participant, candidates);
        }
    }

    public void matchParticipantsWithSpecialFriends() {
        List<Participant> participants = getParticipantsWithLessThanNumberOfMatches(4);
        Queue<Participant> candidates = new LinkedList<>(participants);
        for (Participant participant : participants) {
            matchParticipantWithCandidates(participant, candidates);
        }
    }

    public void matchParticipantWithCandidates(Participant participant, Queue<Participant> candidates) {
        int tries = 0;
        while (participant.getNumberOfMatches() < maxMatches && tries < MAX_TRIES && !candidates.isEmpty()) {
            Participant partner = candidates.poll();
            tries++;
            if (isValidMatching(participant, partner)) {
                addMatching(participant, partner);
                if (partner.getNumberOfMatches() < maxMatches) {
                    candidates.add(partner);
                }
            } else {
                candidates.add(partner);
            }
        }
    }

    public void addMatching(Participant participant, Participant partner) {
        participant.addToMatchingList(new ServiceModelMatching(partner));
        partner.addToMatchingList(new ServiceModelMatching(participant ));
    }

    public Map createNewLanguageQueuesWithCandidates() {
        Map<String, Queue<Participant>> languageQueues = createLanguageQueues(languages);
        addParticipantToLanguageQueues(languageQueues);
        return languageQueues;
    }

    public Map<String, Queue<Participant>> createLanguageQueues(List<String> languages) {
        Map<String, Queue<Participant>> languageQueues = new HashMap<>();
        for (String language : languages) {
            languageQueues.put(language, new LinkedList<>());
        }
        return languageQueues;
    }

    public void addParticipantToLanguageQueues(Map<String, Queue<Participant>> languageQueues) {
        for (Participant participant : matchingParticipants) {
            String language = participant.getFirstPreferLanguage();
            if (!languageQueues.containsKey(language)) {
                languageQueues.put(language, new LinkedList<>());
            }
            languageQueues.get(language).add(participant);
        }
    }

    public List<Participant> getParticipantsWithLessThanNumberOfMatches(int numberOfMatches) {
        return matchingParticipants.stream()
                .filter(p -> p.getNumberOfMatches() < numberOfMatches)
                .collect(Collectors.toList());
    }

    public boolean isValidMatching(Participant participant, Participant matchedParticipant) {
        return matchedParticipant != participant &&
                !isServiceModelMatchigListContainsParticipantId(participant.getServiceModelMatchingList(), matchedParticipant.getId()) &&
                !isServiceModelMatchigListContainsParticipantId(matchedParticipant.getServiceModelMatchingList(), participant.getId()) &&
                matchedParticipant.getNumberOfMatches() < maxMatches;
    }

    public boolean isAllMatched() {
        return matchingParticipants.stream().allMatch(p -> p.getNumberOfMatches() >= 3);
    }

    private boolean isServiceModelMatchigListContainsParticipantId(List<ServiceModelMatching> serviceModelMatchingList, Long participantId){
        for(ServiceModelMatching serviceModelMatching : serviceModelMatchingList){
            if(serviceModelMatching.getPartner().getId() == participantId) return true;
        }

        return false;
    }
}


