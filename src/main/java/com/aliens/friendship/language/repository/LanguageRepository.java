package com.aliens.friendship.language.repository;

import com.aliens.friendship.language.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
}