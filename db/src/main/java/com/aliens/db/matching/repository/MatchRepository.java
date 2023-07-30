package com.aliens.db.matching.repository;

import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<MatchingEntity, Long> {

    Optional<MatchingEntity> findByMatchingMemberAndMatchedMemberAndMatchingDate(MemberEntity matchingMember, MemberEntity matchedMember, Instant matchingDate);

    List<MatchingEntity> findAllByCreatedAtBetweenAndMatchingMember(Instant start, Instant end, MemberEntity matchingMemberEntity);

    List<MatchingEntity> findAllByMatchingMember(MemberEntity matchingMember);
}