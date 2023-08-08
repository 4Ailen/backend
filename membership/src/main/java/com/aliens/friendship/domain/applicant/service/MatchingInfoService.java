package com.aliens.friendship.domain.applicant.service;


import com.aliens.db.applicant.repository.ApplicantRepository;
import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.matching.repository.MatchRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.member.exception.WithdrawnMemberWithinAWeekException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingInfoService {

    private final ApplicantRepository applicantRepository;
    private final MatchRepository matchRepository;



    @Transactional
    public void validateApplied(MemberEntity memberEntity) {
        boolean ApplicantPresent = applicantRepository.findById(memberEntity.getId()).isPresent();
        if (memberEntity.getStatus().equals(MemberEntity.Status.NOT_APPLIED) && !ApplicantPresent) {
            memberEntity.updateStatus(MemberEntity.Status.APPLIED);
        }
        else if (memberEntity.getStatus().equals(MemberEntity.Status.WITHDRAWN)) {
            throw  new WithdrawnMemberWithinAWeekException();
        }

    }



    public List<MatchingEntity> getMatchingResultsByDateAndMemberEntity(Instant matchedDate, MemberEntity matchingMemberEntity) {
        Instant yesterdayOfMatchedDate = matchedDate.minus(6, ChronoUnit.DAYS);
        List<MatchingEntity> matchingEntities = matchRepository.
                findAllByCreatedAtBetweenAndMatchingMember(
                        yesterdayOfMatchedDate
                        ,matchedDate
                        ,matchingMemberEntity
                );
        return matchingEntities;
    }
}