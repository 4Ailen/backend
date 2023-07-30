package com.aliens.friendship.domain.member.controller;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.auth.business.AuthBusiness;
import com.aliens.friendship.domain.member.business.MemberBusiness;
import com.aliens.friendship.domain.member.controller.dto.*;
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
    private final AuthBusiness authBusiness;
    private final MemberBusiness memberBusiness;


    @PostMapping()
    public ResponseEntity<CommonResult> join(@Valid JoinRequestDto joinRequestDto) throws Exception {
        memberBusiness.join(joinRequestDto);
        return ResponseEntity.ok(
                CommonResult.of(
                        "회원가입 성공"
                )
        );
    }

    @GetMapping()
    public ResponseEntity<SingleResult<MemberInfoResponseDto>> getMemberInfo() throws Exception {
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 사용자 정보를 조회하였습니다.",
                        memberBusiness.getMemberInfo()
                )
        );
    }


    @DeleteMapping("/withdraw")
    public ResponseEntity<CommonResult> withdraw(
            @RequestBody Map<String, String> password
    ) throws Exception {
        memberBusiness.withdraw(password.get("password"));
//        authBusiness.logout(accessToken);
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

        memberBusiness.issueTemporaryPassword(email, nameMap.get("name"));

        return ResponseEntity.ok(
                CommonResult.of(
                        "임시 비밀번호 발급 성공"
                )
        );
    }


    @PutMapping("/password")
    public ResponseEntity<CommonResult> changePassword(@Valid @RequestBody PasswordUpdateRequestDto newPasswordDto) throws Exception {
        memberBusiness.changePassword(newPasswordDto);
        return ResponseEntity.ok(
                CommonResult.of(
                        "비밀번호 변경 성공"
                )
        );
    }


    @PatchMapping()
    public ResponseEntity<CommonResult> changeProfileMbti(@RequestBody Map<String, MemberEntity.Mbti> mbti) throws Exception {

        memberBusiness.changeMbti(mbti.get("mbti"));

        return ResponseEntity.ok(
                CommonResult.of(
                        " mbti 값 변경 성공"
                )
        );
    }


    @PutMapping("/profile-image")
    public ResponseEntity<CommonResult> changeProfileImage(@ModelAttribute @Valid ProfileImageRequestDto request) throws Exception {
        memberBusiness.changeProfileImage(request.getProfileImage());
        return ResponseEntity.ok(
                CommonResult.of(
                        "프로필 이미지 수정에 성공하였습니다."
                )
        );
    }


    @PutMapping("/self-introduction")
    public ResponseEntity<CommonResult> changeSelfIntroduction(@ModelAttribute @Valid String selfIntroductionChangeRequest) throws Exception {
        memberBusiness.changeSelfIntroduction(selfIntroductionChangeRequest);
        return ResponseEntity.ok(
                CommonResult.of(
                        "프로필 자기소개 수정에 성공하였습니다."
                )
        );
    }

}