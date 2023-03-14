package com.aliens.friendship.matching.repository;

import com.aliens.friendship.matching.domain.MatchingParticipant;
import com.aliens.friendship.matching.service.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface MatchingParticipantRepository extends JpaRepository<MatchingParticipant, Integer> {
    default List<Participant> findAllParticipants() {
        List<MatchingParticipant> matchingParticipants = findAll();
        List<Participant> participants = new ArrayList<>();
        for (MatchingParticipant matchingParticipant : matchingParticipants) {
            Participant participant = new Participant(
                    matchingParticipant.getId(),
                    matchingParticipant.getFirstPreferLanguage().getLanguageText(),
                    matchingParticipant.getSecondPreferLanguage().getLanguageText()
            );
            participants.add(participant);
        }
        return participants;
    }
}