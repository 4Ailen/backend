package com.aliens.friendship.domain.matching.controller;

import com.aliens.friendship.domain.chatting.service.ChattingService;
import com.aliens.friendship.domain.matching.controller.dto.*;
import com.aliens.friendship.domain.matching.service.BlockingInfoService;
import com.aliens.friendship.domain.matching.service.MatchingInfoService;
import com.aliens.friendship.domain.matching.service.MatchingService;
import com.aliens.friendship.domain.matching.service.ReportService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ResponseService;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingInfoService matchingInfoService;
    private final BlockingInfoService blockingInfoService;
    private final ChattingService chattingService;
    private final MatchingService matchingService;

    private final ReportService reportService;
    private final ResponseService responseService;

    @GetMapping("/languages")
    public SingleResult<Map<String, Object>> getLanguages() {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 언어리스트를 조회하였습니다.",
                matchingInfoService.getLanguages()
        );
    }

    @PostMapping("/applicant")
    public CommonResult applyMatching(@RequestBody ApplicantRequest applicantRequest) {
        matchingInfoService.applyMatching(applicantRequest);

        return responseService.getSuccessResult(
                OK.value(),
                "성공적으로 매칭 신청이 완료되었습니다."
        );
    }

    @GetMapping("/status")
    public SingleResult<Map<String, String>> getStatus() {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 매칭상태가 조회되었습니다.",
                matchingInfoService.getMatchingStatus()
        );
    }

    @GetMapping("/partners")
    public SingleResult<PartnersResponse> getPartners() {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 파트너가 조회되었습니다.",
                matchingInfoService.getPartnersResponse()
        );
    }

    @GetMapping("/applicant")
    public SingleResult<ApplicantResponse> getApplicant() throws Exception {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 매칭 신청자가 조회되었습니다.",
                matchingInfoService.getApplicant()
        );
    }

    @PostMapping("/partner/{memberId}/block")
    public CommonResult blocking(
            @PathVariable Integer memberId,
            @RequestBody Long roomId
    ) {
        blockingInfoService.block(memberId);
        chattingService.blockChattingRoom(roomId);
        return responseService.getSuccessResult(
                OK.value(),
                "차단 완료"
        );
    }

    @PostMapping("/partner/{memberId}/report")
    public CommonResult report(
            @PathVariable Integer memberId,
            @RequestBody ReportRequest reportRequest
    ) {
        reportService.report(memberId, reportRequest);
        return responseService.getSuccessResult(
                OK.value(),
                "신고 완료"
        );
    }

    @GetMapping("/reports")
    public SingleResult<ReportResponse> getReportsByAdmin() {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 신고 목록을 조회하였습니다.",
                reportService.getReportsByAdmin()
        );
    }

    @PostMapping()
    public void match() {
        matchingService.matchParticipants();
    }

    // TODO: 새로 매칭 시작 전 member의 is_applied를 none으로 변경 후 matchingParticipants 데이터 모두 삭제
    // TODO: 특정 시간이 되면 매칭 로직을 돌린다...

    @GetMapping("/completion-date")
    public SingleResult<Map<String, String>> getMatchingRemainingPeriod() throws ParseException {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 매칭 완료 일시가 조회되었습니다.",
                matchingInfoService.getMatchingCompletionDate()
        );
    }
}