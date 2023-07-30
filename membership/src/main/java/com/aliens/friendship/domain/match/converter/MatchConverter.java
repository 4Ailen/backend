package com.aliens.friendship.domain.match.converter;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.friendship.domain.applicant.service.model.Participant;
import com.aliens.friendship.global.common.annotation.Converter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Converter
public class MatchConverter {

    public List<Participant> toParticipants(List<ApplicantEntity> applicantEntities) {
        List<Participant> participants = new ArrayList<>();
        for (ApplicantEntity applicantEntity : applicantEntities) {
            Participant participant = new Participant(
                    applicantEntity.getId(),
                    applicantEntity.getFirstPreferLanguage().toString(),
                    applicantEntity.getSecondPreferLanguage().toString()
            );
            participants.add(participant);
        }
        return participants;
    }
}
