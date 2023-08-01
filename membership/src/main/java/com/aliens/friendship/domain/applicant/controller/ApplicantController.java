package com.aliens.friendship.domain.applicant.controller;

import com.aliens.db.applicant.repository.ApplicantRepository;
import com.aliens.db.matching.repository.MatchRepository;
import com.aliens.friendship.domain.applicant.business.ApplicantBusiness;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantRequestDto;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantResponseDto;
import com.aliens.friendship.domain.applicant.controller.dto.PartnersResponseDto;
import com.aliens.friendship.domain.match.business.MatchBusiness;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/applicant")
@RequiredArgsConstructor
@Slf4j
public class ApplicantController {

    private final MatchBusiness matchBusiness;
    private final ApplicantBusiness applicantBusiness;
    private final MatchRepository matchRepository;
    private final ApplicantRepository applicantRepository;

    @PostMapping()
    public ResponseEntity<CommonResult> applyMatching(@RequestBody ApplicantRequestDto applicantRequestDto) throws Exception {
        applicantBusiness.applyMatching(applicantRequestDto);
        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 매칭 신청이 완료되었습니다."
                )
        );
    }

    @GetMapping()
    public ResponseEntity<SingleResult<ApplicantResponseDto>> getApplicant() throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 매칭 신청자가 조회되었습니다.",
                        applicantBusiness.getApplicant()
                )
        );
    }

    @GetMapping("/status")
    public ResponseEntity<SingleResult<Map<String, String>>> getStatus() throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 매칭상태가 조회되었습니다.",
                        applicantBusiness.getMatchingStatus()
                )
        );
    }


    @GetMapping("/completion-date")
    public ResponseEntity<SingleResult<Map<String, String>>> getMatchingRemainingPeriod() throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 매칭 완료 일시가 조회되었습니다.",
                        applicantBusiness.getMatchingDate()
                )
        );
    }

    @GetMapping("/partners")
    public ResponseEntity<SingleResult<PartnersResponseDto>> getPartners() throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 파트너가 조회되었습니다.",
                        applicantBusiness.getPartnersResponse()
                )
        );
    }




}
