package com.aliens.friendship.repository;

import com.aliens.friendship.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
}