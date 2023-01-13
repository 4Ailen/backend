package com.aliens.friendship.repository;

import com.aliens.friendship.domain.MatchingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingParticipantRepository extends JpaRepository<MatchingParticipant, Integer> {
}