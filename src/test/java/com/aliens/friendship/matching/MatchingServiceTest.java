package com.aliens.friendship.matching;

import com.aliens.friendship.matching.repository.LanguageRepository;
import com.aliens.friendship.matching.repository.MatchingParticipantRepository;
import com.aliens.friendship.matching.service.MatchingService;
import com.aliens.friendship.matching.service.model.Participant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchingServiceTest {

    @Mock
    LanguageRepository languageRepository;

    @Mock
    MatchingParticipantRepository matchingParticipantRepository;

    @InjectMocks
    MatchingService matchingService;

    @Test
    void matchApplicants() {
        // given: 언어 목록, 매칭 참여자 목록 생성
        int numberOfLanguages = 5;
        int numberOfParticipants = 30;
        MatchingTestMockData matchingTestMockData = new MatchingTestMockData(numberOfLanguages, numberOfParticipants);
        when(languageRepository.findAllLanguageTexts()).thenReturn(matchingTestMockData.getMockLanguages());
        when(matchingParticipantRepository.findAllParticipants()).thenReturn(matchingTestMockData.getMockParticipants());

        // when: 매칭 수행
        matchingService.matchParticipants();

        // then: 매칭 결과 검증
        List<Participant> participants = matchingService.getMatchingParticipants();
        for (Participant participant : participants) {
            // 모든 참가자가 적어도 3명의 참가자와 매칭되었는지 확인
            assertThat(participant.getNumberOfMatches()).isGreaterThanOrEqualTo(3);
            // 참가자의 matchingList에 중복된 매칭이 없는지 확인
            assertThat(participant.getMatchingList()).doesNotHaveDuplicates();
            // 참가자의 matchingList에 자기 자신이 없는지 확인
            assertThat(participant.getMatchingList()).doesNotContain(participant);
            // 매칭된 상대의 matchingList에 자기 자신이 있는지 확인
            for (Participant matchingParticipant : participant.getMatchingList()) {
                assertThat(matchingParticipant.getMatchingList()).contains(participant);
            }
        }
    }
}