package com.aliens.friendship.question.repository;

import com.aliens.friendship.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question findByIsSelected(byte isSelected);
}