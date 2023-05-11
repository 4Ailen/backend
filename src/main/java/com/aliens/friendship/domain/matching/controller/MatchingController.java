package com.aliens.friendship.domain.matching.controller;

import com.aliens.friendship.domain.chatting.service.ChattingService;
import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantResponse;
import com.aliens.friendship.domain.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.domain.matching.service.BlockingInfoService;
import com.aliens.friendship.domain.matching.service.MatchingInfoService;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantRequest;
import com.aliens.friendship.domain.matching.service.MatchingService;
import com.aliens.friendship.domain.matching.service.ReportService;
import com.aliens.friendship.domain.matching.controller.dto.ReportRequest;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("api/v1/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingInfoService matchingInfoService;
    private final BlockingInfoService blockingInfoService;
    private final ChattingService chattingService;
    private final MatchingService matchingService;

    private final ReportService reportService;

    @GetMapping("/languages")
    public Response<Map<String, Object>> getLanguages() {
        return Response.SUCCESS(matchingInfoService.getLanguages());
    }

    @PostMapping("/applicant")
    public void applyMatching(@RequestBody ApplicantRequest applicantRequest) {
        matchingInfoService.applyMatching(applicantRequest);
    }

    @GetMapping("/status")
    public Response<Map<String, String>> getStatus() {
        return Response.SUCCESS(matchingInfoService.getMatchingStatus());
    }

    @GetMapping("/partners")
    public Response<PartnersResponse> getPartners() {
        return Response.SUCCESS(matchingInfoService.getPartnersResponse());
    }

    @GetMapping("/applicant")
    public Response<ApplicantResponse> getApplicant() throws Exception {
        return Response.SUCCESS(matchingInfoService.getApplicant());
    }

    @PostMapping("/partner/{memberId}/block")
    public Response<String> blocking(@PathVariable Integer memberId, @RequestBody Long roomId) {
        blockingInfoService.block(memberId);
        chattingService.blockChattingRoom(roomId);
        return Response.SUCCESS("차단 완료");
    }

    @PostMapping("/partner/{memberId}/report")
    public Response<String> report(@PathVariable Integer memberId, @RequestBody ReportRequest reportRequest) {
        reportService.report(memberId, reportRequest);
        return Response.SUCCESS("신고 완료");
    }

    @PostMapping()
    public void match() {
        matchingService.matchParticipants();
    }

    // TODO: 새로 매칭 시작 전 member의 is_applied를 none으로 변경 후 matchingParticipants 데이터 모두 삭제
    // TODO: 특정 시간이 되면 매칭 로직을 돌린다...
}
