package com.aliens.friendship.matching.controller;

import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.matching.domain.Question;
import com.aliens.friendship.matching.service.BlockingInfoService;
import com.aliens.friendship.matching.service.MatchingService;
import com.aliens.friendship.matching.service.model.MatchedGroup;
import com.aliens.friendship.matching.service.model.MatchingParticipantInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final BlockingInfoService blockingInfoService;


    @GetMapping("/matching/languages")
    public Response<Map<String, Object>> getLanguages() {
        return Response.SUCCESS(matchingService.getLanguages());
    }

    @GetMapping("/matching/question")
    public Response<Question> getQuestion() {
        return Response.SUCCESS(matchingService.chooseQuestion());
    }

    @PostMapping("/matching/applicant")
    public void applyMatching(@RequestBody MatchingParticipantInfo matchingParticipant) {
        matchingService.applyMatching(matchingParticipant);
    }

    @GetMapping("/matching/status")
    public Response<Map<String, String>> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", matchingService.checkStatus());

        return Response.SUCCESS(status);
    }

    @GetMapping("/matching/result")
    public List<MatchedGroup> matchingTeams() {
        // TODO: 본인 제외하고 반환
        return matchingService.teamBuilding();
    }

    @GetMapping("/matching/partner/{memberId}/block")
    public Response<String> blocking(@PathVariable int memberId)throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        blockingInfoService.block(email,memberId);
        return Response.SUCCESS("차단 성공");
    }

    // TODO: 새로 매칭 시작 전 member의 is_applied를 none으로 변경 후 matchingParticipants 데이터 모두 삭제
    // TODO: 특정 시간이 되면 매칭 로직을 돌린다...
}
