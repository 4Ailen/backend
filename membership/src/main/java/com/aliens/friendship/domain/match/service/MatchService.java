package com.aliens.friendship.domain.match.service;

import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.matching.repository.MatchRepository;
import com.aliens.db.member.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;

    @Transactional
    public void save(MatchingEntity matchingEntity) {
        matchRepository.save(matchingEntity);
    }


    public Optional<MatchingEntity> findByMatchedMemberAndMatchingMemberReverseWithMatchingDate(MemberEntity matchedMemberEntity, MemberEntity matchingMemberEntity, Instant matchingDate) {
        return matchRepository.findByMatchingMemberAndMatchedMemberAndMatchingDate(matchedMemberEntity,matchingMemberEntity,matchingDate);
    }

    public List<MatchingEntity> findByMemberEntity(MemberEntity loginMemberEntity) {
        List<MatchingEntity> matchEntities = matchRepository.findAllByMatchingMember(loginMemberEntity);
        return matchEntities;
    }
}
