package com.aliens.friendship.matching.repository;

import com.aliens.friendship.matching.domain.Matching;
import com.aliens.friendship.matching.domain.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Integer>, MatchingCustomRepository {

    List<Matching> findByApplicant(Applicant applicant);
}