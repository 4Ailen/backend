package com.aliens.friendship.matching.repository;

import com.aliens.friendship.matching.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question findByIsSelected(byte isSelected);
}