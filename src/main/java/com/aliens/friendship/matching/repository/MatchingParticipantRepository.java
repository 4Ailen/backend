package com.aliens.friendship.matching.repository;

import com.aliens.friendship.matching.domain.MatchingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingParticipantRepository extends JpaRepository<MatchingParticipant, Integer> {
}