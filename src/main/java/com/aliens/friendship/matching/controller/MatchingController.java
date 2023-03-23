package com.aliens.friendship.matching.controller;

import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.matching.controller.dto.ApplicantResponse;
import com.aliens.friendship.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.matching.service.MatchingInfoService;
import com.aliens.friendship.matching.controller.dto.ApplicantRequest;
import com.aliens.friendship.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingInfoService matchingInfoService;
    private final MatchingService matchingService;

    @GetMapping("/matching/languages")
    public Response<Map<String, Object>> getLanguages() {
        return Response.SUCCESS(matchingInfoService.getLanguages());
    }

    @PostMapping("/matching/applicant")
    public void applyMatching(@RequestBody ApplicantRequest applicantRequest) {
        matchingInfoService.applyMatching(applicantRequest);
    }

    @GetMapping("/matching/status")
    public Response<Map<String, String>> getStatus() {
        return Response.SUCCESS(matchingInfoService.getMatchingStatus());
    }

    @GetMapping("/matching/partners")
    public Response<PartnersResponse> getPartners() {
        return Response.SUCCESS(matchingInfoService.getPartnersResponse());
    }

    @GetMapping("/matching/applicant")
    public Response<ApplicantResponse> getApplicant() throws Exception {
        return Response.SUCCESS(matchingInfoService.getApplicant());
    }

    @PostMapping("/matching")
    public void match() {
        matchingService.matchParticipants();
    }

    // TODO: 새로 매칭 시작 전 member의 is_applied를 none으로 변경 후 matchingParticipants 데이터 모두 삭제
    // TODO: 특정 시간이 되면 매칭 로직을 돌린다...
}
