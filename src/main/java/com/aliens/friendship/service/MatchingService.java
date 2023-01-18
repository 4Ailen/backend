package com.aliens.friendship.service;

import com.aliens.friendship.domain.Language;
import com.aliens.friendship.domain.Question;
import com.aliens.friendship.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
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

    public Question chooseQuestion() {
        Question currentQuestion, nextQuestion;
        List<Question> questions;
        int firstQId, lastQId, idx = 1;

        // 현재 isSelected가 1로 설정된 질문을 가져온 후 1을 0으로 변경
        currentQuestion = questionRepository.findByIsSelected((byte) 1);
        currentQuestion.setIsSelected((byte) 0);
        questionRepository.save(currentQuestion);

        questions = questionRepository.findAll();
        firstQId = questions.stream().min(Comparator.comparingInt(Question::getId)).get().getId();
        lastQId = questions.stream().max(Comparator.comparingInt(Question::getId)).get().getId();

        // 다음 질문의 isSelected 값을 1로 변경
        while (true) {
            if (currentQuestion.getId() + idx > lastQId) {
                nextQuestion = questionRepository.findById(firstQId).get();
            } else {
                nextQuestion = questionRepository.findById(currentQuestion.getId() + idx).get();
            }
            // (다음 질문의 id)가 (현재 질문 id+1)이 아닐수도 있음.
            if (nextQuestion == null) {
                idx++;
            } else {
                break;
            }
        }
        nextQuestion.setIsSelected((byte) 1);
        questionRepository.save(nextQuestion);

        return currentQuestion;
    }

}
