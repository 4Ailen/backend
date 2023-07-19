package com.aliens.friendship.domain.member.controller;

import com.aliens.friendship.domain.auth.service.AuthService;
import com.aliens.friendship.domain.member.controller.dto.*;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    /**
     * 회원가입 성공시 성공했다는 결과만 응답
     */
    @PostMapping()
    public ResponseEntity<CommonResult> join(@Valid JoinDto joinDto) throws Exception {
        memberService.join(joinDto);
        return ResponseEntity.ok(
                CommonResult.of(
                        "회원가입 성공"
                )
        );
    }

    @GetMapping()
    public ResponseEntity<SingleResult<MemberInfoDto>> getMemberInfo() throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 사용자 정보를 조회하였습니다.",
                        memberService.getMemberInfo()
                )
        );
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<CommonResult> withdraw(
            @RequestBody Map<String, String> password,
            @RequestHeader("Authorization") String accessToken
    ) throws Exception {
        memberService.withdraw(password.get("password"));
        authService.logout(accessToken);
        return ResponseEntity.ok(
                CommonResult.of(
                        "회원탈퇴 성공"
                )
        );
    }

    @GetMapping("/email/{email}/existence")
    public ResponseEntity<SingleResult<Map<String, Object>>> isJoinedEmail(@PathVariable String email) {
        Map<String, Object> existence = new HashMap<>();
        existence.put("existence", memberService.isJoinedEmail(email));
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 조회하였습니다.",
                        existence
                )
        );
    }

    @PostMapping("/{email}/password/temp")
    public ResponseEntity<CommonResult> issueTemporaryPassword(
            @PathVariable String email,
            @RequestBody Map<String, String> nameMap
    ) throws Exception {
        memberService.issueTemporaryPassword(email, nameMap.get("name"));
        return ResponseEntity.ok(
                CommonResult.of(
                        "임시 비밀번호 발급 성공"
                )
        );
    }

    @PutMapping("/password")
    public ResponseEntity<CommonResult> changePassword(@RequestBody PasswordUpdateRequestDto newPasswordDto) throws Exception {
        memberService.changePassword(newPasswordDto);
        return ResponseEntity.ok(
                CommonResult.of(
                        "비밀번호 변경 성공"
                )
        );
    }

    @PatchMapping()
    public ResponseEntity<CommonResult> changeProfileMbti(@RequestBody Map<String, Member.Mbti> mbti) {
        memberService.changeProfileNameAndMbti(mbti.get("mbti"));
        return ResponseEntity.ok(
                CommonResult.of(
                        "프로필 이름과 mbti 값 변경 성공"
                )
        );
    }

    @PutMapping("/profile-image")
    public ResponseEntity<CommonResult> changeProfileImage(@ModelAttribute @Valid ProfileImageRequest request) throws Exception {
        memberService.changeProfileImage(request.getProfileImage());
        return ResponseEntity.ok(
                CommonResult.of(
                        "프로필 이미지 수정에 성공하였습니다."
                )
        );
    }

    @PutMapping("/self-introduction")
    public ResponseEntity<CommonResult> changeSelfIntroduction(@ModelAttribute @Valid String selfIntroductionChangeRequest) throws Exception {
        memberService.changeSelfIntroduction(selfIntroductionChangeRequest);
        return ResponseEntity.ok(
                CommonResult.of(
                        "프로필 자기소개 수정에 성공하였습니다."
                )
        );
    }

    @GetMapping("/{email}/authentication-status")
    public ResponseEntity<SingleResult<Map<String, String>>> getMemberAuthenticationStatus(@PathVariable String email) {
        Map<String, String> status = new HashMap<>();
        status.put("status", memberService.getMemberAuthenticationStatus(email));
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 조회하였습니다.",
                        status
                )
        );
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<CommonResult> deleteMemberInfoByAdmin(@PathVariable Integer memberId) {
        memberService.deleteMemberInfoByAdmin(memberId);
        return ResponseEntity.ok(
                CommonResult.of(
                        "회원 관련 정보 삭제에 성공하였습니다."
                )
        );
    }

    @GetMapping("/{memberId}")
    public SingleResult<MemberInfoByAdminDto> getMemberInfoByAdmin(@PathVariable Integer memberId) throws Exception {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 사용자 정보를 조회하였습니다.",
                memberService.getMemberInfoByAdmin(memberId)
        );
    }
}