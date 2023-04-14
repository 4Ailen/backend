package com.aliens.friendship.matching.repository;

import com.aliens.friendship.matching.domain.Applicant;
import com.aliens.friendship.matching.service.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface ApplicantRepository extends JpaRepository<Applicant, Integer> {
    default List<Participant> findAllParticipants() {
        List<Applicant> applicants = findAll();
        List<Participant> participants = new ArrayList<>();
        for (Applicant applicant : applicants) {
            if (applicant.getId() == 1) {
                continue;
            }
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