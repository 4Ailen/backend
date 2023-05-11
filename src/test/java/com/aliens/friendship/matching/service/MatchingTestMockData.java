package com.aliens.friendship.matching.service;

import com.aliens.friendship.domain.matching.service.model.Participant;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class MatchingTestMockData {
    private List<String> mockLanguages = new ArrayList<>();
    private List<Participant> mockParticipants = new ArrayList<>();
    private final String[] languageTexts = {"Korean", "English", "Japanese", "Chinese", "Spanish", "French", "German", "Russian", "Arabic", "Portuguese"};

    public MatchingTestMockData(int numberOfLanguages, int numberOfParticipants) {
        createLanguageTexts(numberOfLanguages);
        createParticipants(numberOfParticipants);
    }

    private void createLanguageTexts(int numberOfLanguages) {
        for (int i = 0; i < numberOfLanguages; i++) {
            mockLanguages.add(languageTexts[i]);
        }
    }

    private void createParticipants(int numberOfParticipants) {
        Random random = new Random();
        for (int i = 1; i <= numberOfParticipants; i++) {
            String firstPreferLanguage = mockLanguages.get(random.nextInt(mockLanguages.size()));
            String secondPreferLanguage;
            do { // 1차와 중복 되지 않는 2차 선호 언어 생성
                secondPreferLanguage = mockLanguages.get(random.nextInt(mockLanguages.size()));
            } while (secondPreferLanguage.equals(firstPreferLanguage));
            mockParticipants.add(new Participant(i, firstPreferLanguage, secondPreferLanguage));
        }
    }
}
