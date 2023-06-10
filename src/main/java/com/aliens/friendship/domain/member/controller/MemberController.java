package com.aliens.friendship.domain.member.controller;

import com.aliens.friendship.domain.auth.service.AuthService;
import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.domain.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.domain.member.controller.dto.ProfileImageRequest;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ResponseService;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;
    private final ResponseService responseService;

    /**
     * 회원가입 성공시 성공했다는 결과만 응답
     */
    @PostMapping()
    public CommonResult join(@Valid JoinDto joinDto) throws Exception {
        memberService.join(joinDto);
        return responseService.getSuccessResult(
                OK.value(),
                "회원가입 성공"
        );
    }

    @GetMapping()
    public SingleResult<MemberInfoDto> getMemberInfo() throws Exception {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 사용자 정보를 조회하였습니다.",
                memberService.getMemberInfo()
        );
    }

    @PostMapping("/withdraw")
    public CommonResult withdraw(
            @RequestBody Map<String, String> password,
            @RequestHeader("Authorization") String accessToken
    ) throws Exception {
        memberService.withdraw(password.get("password"));
        authService.logout(accessToken);
        return responseService.getSuccessResult(
                OK.value(),
                "회원탈퇴 성공"
        );
    }

    @GetMapping("/email/{email}/existence")
    public SingleResult<Map<String, Object>> isJoinedEmail(@PathVariable String email) {
        Map<String, Object> existence = new HashMap<>();
        existence.put("existence", memberService.isJoinedEmail(email));
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 조회하였습니다.",
                existence
        );
    }

    @PostMapping("/{email}/password/temp")
    public CommonResult issueTemporaryPassword(
            @PathVariable String email,
            @RequestBody Map<String, String> nameMap
    ) throws Exception {
        memberService.issueTemporaryPassword(email, nameMap.get("name"));
        return responseService.getSuccessResult(
                OK.value(),
                "임시 비밀번호 발급 성공"
        );
    }

    @PutMapping("/password")
    public CommonResult changePassword(@RequestBody PasswordUpdateRequestDto newPasswordDto) throws Exception {
        memberService.changePassword(newPasswordDto);
        return responseService.getSuccessResult(
                OK.value(),
                "비밀번호 변경 성공"
        );
    }

    @PatchMapping()
    public CommonResult changeProfileMbti(@RequestBody Map<String, Member.Mbti> mbti) {
        memberService.changeProfileNameAndMbti(mbti.get("mbti"));
        return responseService.getSuccessResult(
                OK.value(),
                "프로필 이름과 mbti 값 변경 성공"
        );
    }

    @PutMapping("/profile-image")
    public CommonResult changeProfileImage(@ModelAttribute @Valid ProfileImageRequest request) throws Exception {
        memberService.changeProfileImage(request.getProfileImage());
        return responseService.getSuccessResult(
                OK.value(),
                "프로필 이미지 수정에 성공하였습니다."
        );
    }

    @GetMapping("/{email}/authentication-status")
    public SingleResult<Map<String, String>> getMemberAuthenticationStatus(@PathVariable String email) {
        Map<String, String> status = new HashMap<>();
        status.put("status", memberService.getMemberAuthenticationStatus(email));
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 조회하였습니다.",
                status
        );
    }

    // TODO: 관리자 권한 추가
    @DeleteMapping("/{memberId}")
    public CommonResult deleteMemberInfoByAdmin(@PathVariable Integer memberId) {
        memberService.deleteMemberInfoByAdmin(memberId);
        return responseService.getSuccessResult(
                OK.value(),
                "회원 관련 정보 삭제에 성공하였습니다."
        );
    }
}