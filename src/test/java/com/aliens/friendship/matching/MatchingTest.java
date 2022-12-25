package com.aliens.friendship.matching;

import com.aliens.friendship.dto.ApplicantInfo;
import com.aliens.friendship.dto.MatchedApplicants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

