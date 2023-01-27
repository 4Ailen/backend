package com.aliens.friendship.matchingParticipant.controller;

import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.question.domain.Question;
import com.aliens.friendship.matchingParticipant.MatchingParticipantInfo;
import com.aliens.friendship.matchingParticipant.MatchedGroup;
import com.aliens.friendship.matchingParticipant.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MatchingController {

    private final MatchingService matchingService;

    @Autowired
    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/matching/languages")
    public Response<Map<String, Object>> getLanguages() {
        return Response.SUCCESS(matchingService.getLanguages());
    }

    @GetMapping("/matching/question")
    public Response<Question> getQuestion() {
        return Response.SUCCESS(matchingService.chooseQuestion());
    }

    @PostMapping("/matching/applicant")
    public void applyMatching(@RequestBody MatchingParticipantInfo applicantInfo) {
        matchingService.applyMatching(applicantInfo);
    }

    @GetMapping("/matching/status")
    public Response<Map<String, String>> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", matchingService.checkStatus());

        return Response.SUCCESS(status);
    }

    // TODO: 특정 시간이 되면 매칭 로직을 돌린다...
    @GetMapping("/matching/result")
    public List<MatchedGroup> matchingTeams() {
        return matchingService.teamBuilding();
    }

    // TODO: 새로 매칭 시작 전 member의 is_applied를 none으로 변경 후 matchingParticipants 데이터 모두 삭제
}
