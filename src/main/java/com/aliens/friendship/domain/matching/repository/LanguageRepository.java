package com.aliens.friendship.domain.matching.repository;

import com.aliens.friendship.domain.matching.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Collectors;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    default List<String> findAllLanguageTexts() {
        List<Language> languages = findAll();
        return languages.stream().map(Language::getLanguageText).collect(Collectors.toList());
    }

}