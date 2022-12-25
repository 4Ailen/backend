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
