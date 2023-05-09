package com.aliens.friendship.domain.matching.repository;

import com.aliens.friendship.domain.matching.domain.Applicant;
import com.aliens.friendship.domain.matching.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Integer>, MatchingCustomRepository {

    List<Matching> findByApplicant(Applicant applicant);
}