package com.aliens.friendship.matching;

import com.aliens.friendship.dto.ApplicantInfo;
import com.aliens.friendship.dto.MatchedApplicants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SpringBootTest
public class MatchingTest {

    static List<ApplicantInfo> mockApplicants; // 신청자
    static List<ApplicantInfo> ans1, ans2; // 1차 필터링(질문 기반)
    static List<List<ApplicantInfo>> ans1_lg, ans2_lg; // 2차 필터링(언어 기반)
    static List<MatchedApplicants> matchedTeams; // 팀 반환
    List<ApplicantInfo> remainApplicants1, remainApplicants2;

    @BeforeAll
    static void init() {
        mockApplicants = new ArrayList<>();
        ans1 = new ArrayList<>();
        ans2 = new ArrayList<>();
        ans1_lg = new ArrayList<>();
        ans2_lg = new ArrayList<>();
        matchedTeams = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ans1_lg.add(new ArrayList<>());
            ans2_lg.add(new ArrayList<>());
        }
    }

    @Test
    void matchApplicants() {
        // given
        loadApplicants();

        // when
        Collections.shuffle(mockApplicants);
        filterQuestion();
        filterLanguage(ans1, 1);
        filterLanguage(ans2, 2);
        remainApplicants1 = makeTeam(ans1_lg);
        remainApplicants2 = makeTeam(ans2_lg);

        // then
        // 남은 신청자가 0명인지 확인
        Assertions.assertThat(remainApplicants1.size()).isEqualTo(0);
        Assertions.assertThat(remainApplicants2.size()).isEqualTo(0);
        // 생성되어야 하는 팀 수 확인
        Assertions.assertThat(matchedTeams.size()).isEqualTo(calculateTeamCnt());
    }

    void loadApplicants() {
        Random random = new Random();

        // 신청자 수: 4~52명, 질문 값: 1 또는 2, 언어: 10가지 중 하나
        for (int i = 0; i < 2; i++) {
            mockApplicants.add(new ApplicantInfo(i, 1, random.nextInt(10)));
            mockApplicants.add(new ApplicantInfo(i + 2, 2, random.nextInt(10)));
        }
        for (int i = 4; i < random.nextInt(50) + 3; i++) {
            mockApplicants.add(new ApplicantInfo(i, random.nextInt(2) + 1, random.nextInt(10)));
        }
    }

    void filterQuestion() {
        ans1 = mockApplicants.stream()
                .filter(ans -> ans.getQuestion() == 1)
                .collect(Collectors.toList());

        ans2 = mockApplicants.stream()
                .filter(ans -> ans.getQuestion() == 2)
                .collect(Collectors.toList());
    }

    // ans에 대해 10가지 언어로 나누기
    void filterLanguage(List<ApplicantInfo> ans, int ansNum) {
        for (int i = 0; i < 10; i++) {
            int lgIdx = i;
            List<ApplicantInfo> tmp = ans.stream()
                    .filter(lg -> lg.getLanguage() == lgIdx)
                    .collect(Collectors.toList());
            if (ansNum == 1) {
                ans1_lg.get(i).addAll(tmp);
            } else if (ansNum == 2) {
                ans2_lg.get(i).addAll(tmp);
            }
        }
    }

    List<ApplicantInfo> makeTeam(List<List<ApplicantInfo>> filteredList) {
        List<ApplicantInfo> remainApplicants = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            // 세명씩 팀 구성
            while (filteredList.get(i).size() >= 3) {
                MatchedApplicants team = new MatchedApplicants(filteredList.get(i).get(0).getMemberId(),
                        filteredList.get(i).get(1).getMemberId(),
                        filteredList.get(i).get(2).getMemberId());
                for (int j = 0; j < 3; j++) {
                    filteredList.get(i).remove(0);
                }
                matchedTeams.add(team);
            }
            // 남은 신청자들
            for (int j = 0; j < filteredList.get(i).size(); j++) {
                remainApplicants.add(filteredList.get(i).get(j));
            }
        }

        // 남은 신청자들 1차: 3명씩 팀
        while (remainApplicants.size() >= 3) {
            MatchedApplicants team = new MatchedApplicants(remainApplicants.get(0).getMemberId(),
                    remainApplicants.get(1).getMemberId(),
                    remainApplicants.get(2).getMemberId());
            for (int j = 0; j < 3; j++) {
                remainApplicants.remove(0);
            }
            matchedTeams.add(team);
        }

        // 남은 신청자들 2차: 2명씩 팀
        if (remainApplicants.size() == 2) { // 2명 팀
            MatchedApplicants team = new MatchedApplicants(remainApplicants.get(0).getMemberId(),
                    remainApplicants.get(1).getMemberId(),
                    null);
            for (int j = 0; j < 2; j++) {
                remainApplicants.remove(0);
            }
            // 만들어진 팀 추가
            matchedTeams.add(team);
        } else if (remainApplicants.size() == 1) { // 이전에 만들어진 팀의 3명 + 남은 1명으로 2명, 2명 팀
            int id1, id2, id3, id4, lastIdx = matchedTeams.size() - 1;
            id1 = matchedTeams.get(lastIdx).getMemberId1();
            id2 = matchedTeams.get(lastIdx).getMemberId2();
            id3 = matchedTeams.get(lastIdx).getMemberId3();
            id4 = remainApplicants.get(0).getMemberId();
            matchedTeams.remove(matchedTeams.size() - 1);
            matchedTeams.add(new MatchedApplicants(id1, id2, null));
            matchedTeams.add(new MatchedApplicants(id3, id4, null));
            remainApplicants.remove(0);
        }

        return remainApplicants;
    }

    // 신청자 수 기반으로 생성되어야 할 팀 수 계산
    int calculateTeamCnt() {
        int teamCnt = 0;
        teamCnt += ans1.size() / 3;
        if (ans1.size() % 3 != 0) {
            teamCnt++;
        }
        teamCnt += ans2.size() / 3;
        if (ans2.size() % 3 != 0) {
            teamCnt++;
        }

        return teamCnt;
    }

}