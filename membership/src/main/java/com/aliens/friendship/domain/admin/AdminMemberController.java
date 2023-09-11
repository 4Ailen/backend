package com.aliens.friendship.domain.admin;

import com.aliens.friendship.domain.fcm.service.FcmService;
import com.aliens.friendship.domain.match.business.MatchBusiness;
import com.aliens.friendship.domain.member.business.MemberBusiness;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoByAdminResponseDto;
import com.aliens.friendship.domain.member.controller.dto.ReportResponseDto;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.domain.report.business.ReportBusiness;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;
    private final MemberBusiness memberBusiness;
    private final ReportBusiness reportBusiness;
    private final MatchBusiness matchBusiness;
    private final FcmService fcmService;

    @DeleteMapping("/{memberId}")
    public ResponseEntity<CommonResult> deleteMemberInfoByAdmin(@PathVariable Long memberId) {
        memberService.deleteMemberInfoByAdmin(memberId);
        return ResponseEntity.ok(
                CommonResult.of(
                        "회원 관련 정보 삭제에 성공하였습니다."
                )
        );
    }

    @GetMapping("/{email}")
    public ResponseEntity<SingleResult<MemberInfoByAdminResponseDto>> getMemberInfoByAdmin(@PathVariable String email) throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 사용자 정보를 조회하였습니다.",
                        memberBusiness.getMemberInfoByAdmin(email)
                )
        );
    }

    @GetMapping("/report")
    public ResponseEntity<SingleResult<ReportResponseDto>> getReportsByAdmin() {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 신고 목록을 조회하였습니다.",
                        reportBusiness.getReportsByAdmin()
                )
        );
    }

    // TODO: 특정 시간이 되면 매칭 로직을 돌린다. (현재는 수동)
    // TODO: 새로 매칭 시작 전 member의 is_applied를 none으로 변경 후 matchingParticipants 데이터 모두 삭제
    @PostMapping("match")
    public void match() throws Exception {
        matchBusiness.matchingAllApplicant();
        fcmService.sendNoticeToAll("매칭 완료 알림", "매칭이 완료되었습니다!");
    }

}
