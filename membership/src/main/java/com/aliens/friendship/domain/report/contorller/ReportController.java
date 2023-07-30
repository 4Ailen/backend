package com.aliens.friendship.domain.report.contorller;

import com.aliens.friendship.domain.member.controller.dto.ReportRequestDto;
import com.aliens.friendship.domain.member.controller.dto.ReportResponseDto;
import com.aliens.friendship.domain.report.business.ReportBusiness;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportBusiness reportBusiness;

    @PostMapping("/{memberId}")
    public ResponseEntity<CommonResult> report(
            @PathVariable Long memberId,
            @RequestBody ReportRequestDto reportRequestDto
    ) throws Exception {
        reportBusiness.report(memberId, reportRequestDto);
        return ResponseEntity.ok(
                CommonResult.of(
                        "신고 완료"
                )
        );
    }

}
