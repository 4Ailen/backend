package com.aliens.friendship.domain.matching.repository;

import com.aliens.friendship.domain.matching.domain.Applicant;
import com.aliens.friendship.domain.matching.service.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface ApplicantRepository extends JpaRepository<Applicant, Integer> {
    default List<Participant> findAllParticipants() {
        List<Applicant> applicants = findAll();
        List<Participant> participants = new ArrayList<>();
        for (Applicant applicant : applicants) {
            Participant participant = new Participant(
                    applicant.getId(),
                    applicant.getFirstPreferLanguage().getLanguageText(),
                    applicant.getSecondPreferLanguage().getLanguageText()
            );
            participants.add(participant);
        }
        return participants;
    }
}