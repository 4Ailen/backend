package com.aliens.friendship.matching.service;

import com.aliens.friendship.matching.repository.LanguageRepository;
import com.aliens.friendship.matching.repository.MatchingParticipantRepository;
import com.aliens.friendship.matching.service.model.Participant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final LanguageRepository languageRepository;
    private final MatchingParticipantRepository matchingParticipantRepository;
    private static final int MAX_TRIES = 200;
    private List<String> languages = new ArrayList<>();
    private List<Participant> matchingParticipants = new ArrayList<>();
    private Map<String, Queue<Participant>> languageQueuesWithCandidates = new HashMap<>();
    private int maxMatches = 3;
    private int matchSteps = 0;

    public void matchParticipants() {
        init();
        matchParticipantsWithCandidatesWhoHasFirstPreferLanguages(); maxMatches++; matchSteps++; //1선호언어
        if (!isAllMatched()) {matchParticipantsWithCandidatesWhoHasSecondPreferLanguages(); matchSteps++;} //2선호언어
        if (!isAllMatched()) {matchParticipantsWhoHasLessThanThreeMatchesWithCandidatesWhoHasLessThanFourMatches(); matchSteps++;} //랜덤
        matchParticipantsWithSpecialFriends(); //SF
    }

    private void matchParticipantsWithCandidatesWhoHasFirstPreferLanguages() {
        for (Participant participant : matchingParticipants) {
            Queue<Participant> candidates = languageQueuesWithCandidates.get(participant.getFirstPreferLanguage());
            matchParticipantWithCandidates(participant, candidates);
        }
    }

    private void matchParticipantsWithCandidatesWhoHasSecondPreferLanguages() {
        List<Participant> participants = getParticipantsWithLessThanNumberOfMatches(3);
        languageQueuesWithCandidates = initLanguageQueuesWithCandidates();
        for (Participant participant : participants) {
            Queue<Participant> candidates = languageQueuesWithCandidates.get(participant.getSecondPreferLanguage());
            matchParticipantWithCandidates(participant, candidates);
        }
    }

    private void matchParticipantsWhoHasLessThanThreeMatchesWithCandidatesWhoHasLessThanFourMatches(){
        List<Participant> participants = getParticipantsWithLessThanNumberOfMatches(3);
        Queue<Participant> candidates = new LinkedList<>(getParticipantsWithLessThanNumberOfMatches(4));
        for (Participant participant : participants) {
            matchParticipantWithCandidates(participant, candidates);
        }
    }

    //4명이하의 사람들에게만 매칭, 이미 4명은 specialFriend와 매칭이 된 상태
    // 즉 3명이하의 사람들끼리 4명이 되도록 매칭
    private void matchParticipantsWithSpecialFriends(){
        List<Participant> participants = getParticipantsWithLessThanNumberOfMatches(4);
        Queue<Participant> candidates = new LinkedList<>(participants);
        for (Participant participant : participants) {
            matchParticipantWithCandidates(participant, candidates);
        }
    }

    // break 대신 while문에 조건을 추가, continue를 대신 if문 로직으로 대체하여 코드 가독성을 높여보았습니다.
    private void matchParticipantWithCandidates(Participant participant, Queue<Participant> candidates) {
        int tries = 0;
        while (participant.getNumberOfMatches() < maxMatches && tries < MAX_TRIES && !candidates.isEmpty()) {
            Participant matchedParticipant = candidates.poll();
            tries++;

            if (isValidMatching(participant, matchedParticipant)) {
                participant.addToMatchingList(matchedParticipant);
                matchedParticipant.addToMatchingList(participant);

                if (matchedParticipant.getNumberOfMatches() < maxMatches) {
                    candidates.add(matchedParticipant);
                }
            } else {
                candidates.add(matchedParticipant);
            }
        }
    }

    private void init() {
        languages = languageRepository.findLanguageTexts();
        matchingParticipants = matchingParticipantRepository.findAllParticipants();
        languageQueuesWithCandidates = initLanguageQueuesWithCandidates();
    }

    private Map initLanguageQueuesWithCandidates() {
        Map<String, Queue<Participant>> languageQueues = new HashMap<>();
        for (String language : languages) {
            languageQueues.put(language, new LinkedList<>());
        }
        for (Participant participant : matchingParticipants) {
            String language = participant.getFirstPreferLanguage();
            if (!languageQueues.containsKey(language)) {
                languageQueues.put(language, new LinkedList<>());
            }
            languageQueues.get(language).add(participant);
        }
        return languageQueues;
    }

    private List<Participant> getParticipantsWithLessThanNumberOfMatches(int numberOfMatches) {
        return matchingParticipants.stream()
                .filter(p -> p.getNumberOfMatches() < numberOfMatches)
                .collect(Collectors.toList());
    }

    private boolean isValidMatching(Participant participant, Participant matchedParticipant) {
        return matchedParticipant != participant &&
                !participant.getMatchingList().contains(matchedParticipant) &&
                !matchedParticipant.getMatchingList().contains(participant) &&
                matchedParticipant.getNumberOfMatches() < maxMatches;
    }

    private boolean isAllMatched() {
        return matchingParticipants.stream().allMatch(p -> p.getNumberOfMatches() >= 3);
    }

    public List<Participant> getMatchingParticipants() {
        return matchingParticipants;
    }

    @Deprecated
    public void participantsMatchingStatus(){
        for (Participant participant : matchingParticipants) {
            System.out.println("참가자 " + participant.getId() + "의 매칭된 참가자 수: " + participant.getNumberOfMatches());
            for (Participant matchedParticipant : participant.getMatchingList()) {
                System.out.println("참가자 " + participant.getId() + " - 참가자 " + matchedParticipant.getId());
            }
        }
        System.out.println("매칭 참가자 수: " + matchingParticipants.size());
        System.out.println("매칭 단계: " + matchSteps);
        System.out.println("매칭이 안된 참가자: ");
        for (Participant participant : matchingParticipants) {
            if (participant.getNumberOfMatches() < 3) {
                System.out.println("참가자 " + participant.getId());
            }
        }
    }
}


