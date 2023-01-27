package com.aliens.friendship.matchingParticipant.repository;

import com.aliens.friendship.matchingParticipant.domain.MatchingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingParticipantRepository extends JpaRepository<MatchingParticipant, Integer> {
}