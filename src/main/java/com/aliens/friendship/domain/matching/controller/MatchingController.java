package com.aliens.friendship.domain.matching.controller;

import com.aliens.friendship.domain.chatting.service.ChattingService;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantRequest;
import com.aliens.friendship.domain.matching.controller.dto.ApplicantResponse;
import com.aliens.friendship.domain.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.domain.matching.controller.dto.ReportRequest;
import com.aliens.friendship.domain.matching.service.BlockingInfoService;
import com.aliens.friendship.domain.matching.service.MatchingInfoService;
import com.aliens.friendship.domain.matching.service.MatchingService;
import com.aliens.friendship.domain.matching.service.ReportService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingInfoService matchingInfoService;
    private final BlockingInfoService blockingInfoService;
    private final ChattingService chattingService;
    private final MatchingService matchingService;

    private final ReportService reportService;

    @GetMapping("/languages")
    public ResponseEntity<SingleResult<Map<String, Object>>> getLanguages() {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 언어리스트를 조회하였습니다.",
                        matchingInfoService.getLanguages()
                )
        );
    }

    @GetMapping("/applicant")
    public ResponseEntity<SingleResult<ApplicantResponse>> getApplicant() throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 매칭 신청자가 조회되었습니다.",
                        matchingInfoService.getApplicant()
                )
        );
    }

    @PostMapping("/applicant")
    public ResponseEntity<CommonResult> applyMatching(@RequestBody ApplicantRequest applicantRequest) {
        matchingInfoService.applyMatching(applicantRequest);

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 매칭 신청이 완료되었습니다."
                )
        );
    }

    @GetMapping("/status")
    public ResponseEntity<SingleResult<Map<String, String>>> getStatus() {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 매칭상태가 조회되었습니다.",
                        matchingInfoService.getMatchingStatus()
                )
        );
    }

    @GetMapping("/partners")
    public ResponseEntity<SingleResult<PartnersResponse>> getPartners() {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 파트너가 조회되었습니다.",
                        matchingInfoService.getPartnersResponse()
                )
        );
    }

    @PostMapping("/partner/{memberId}/block")
    public ResponseEntity<CommonResult> blocking(
            @PathVariable Integer memberId,
            @RequestBody Long roomId
    ) {
        blockingInfoService.block(memberId);
        chattingService.blockChattingRoom(roomId);
        return ResponseEntity.ok(
                CommonResult.of(
                        "차단 완료"
                )
        );
    }

    @PostMapping("/partner/{memberId}/report")
    public ResponseEntity<CommonResult> report(
            @PathVariable Integer memberId,
            @RequestBody ReportRequest reportRequest
    ) {
        reportService.report(memberId, reportRequest);
        return ResponseEntity.ok(
                CommonResult.of(
                        "신고 완료"
                )
        );
    }

    @PostMapping()
    public void match() {
        matchingService.matchParticipants();
    }

    // TODO: 새로 매칭 시작 전 member의 is_applied를 none으로 변경 후 matchingParticipants 데이터 모두 삭제
    // TODO: 특정 시간이 되면 매칭 로직을 돌린다...

    @GetMapping("/remaining-period")
    public ResponseEntity<SingleResult<Map<String, String>>> getMatchingRemainingPeriod() throws ParseException {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 매칭까지 남은 시간이 조회되었습니다.",
                        matchingInfoService.getMatchingRemainingPeroid()
                )
        );
    }
}