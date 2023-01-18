package com.aliens.friendship.service;

import com.aliens.friendship.domain.Language;
import com.aliens.friendship.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final LanguageRepository languageRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final MatchingParticipantRepository matchingParticipantRepository;
    private final BlockingInfoRepository blockingInfoRepository;

    public List<Language> getLanguages() {
        return languageRepository.findAll();
    }

}
