package com.aliens.friendship.matching.repository;

import com.aliens.friendship.matching.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
}