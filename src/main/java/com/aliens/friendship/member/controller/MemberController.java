package com.aliens.friendship.member.controller;

import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.member.controller.dto.PasswordUpdateRequestDto;
import com.aliens.friendship.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping()
    public Response<String> join(JoinDto joinDto) throws Exception {
        memberService.join(joinDto);
        return Response.SUCCESS("회원가입 성공");
    }

    @GetMapping()
    public Response<MemberInfoDto> getMemberInfo() throws Exception {
        return Response.SUCCESS(memberService.getMemberInfo());
    }

    @PostMapping("/withdraw")
    public Response<String> withdraw(@RequestBody String password) throws Exception {
        memberService.withdraw(password);
        return Response.SUCCESS("회원탈퇴 성공");
    }

    @GetMapping("/email/{email}/existence")
    public Response<Map<String, Object>> isJoinedEmail(@PathVariable String email) {
        Map<String, Object> existence = new HashMap<>();
        existence.put("existence", memberService.isJoinedEmail(email));
        return Response.SUCCESS(existence);
    }

    @PostMapping("/{email}/password/temp")
    public Response<String> issueTemporaryPassword(@PathVariable String email, @RequestBody Map<String, String> nameMap) throws Exception {
        memberService.issueTemporaryPassword(email, nameMap.get("name"));
        return Response.SUCCESS("임시 비밀번호 발급 성공");
    }

    @PutMapping("/password")
    public Response<String> changePassword(@RequestBody PasswordUpdateRequestDto newPasswordDto) throws Exception {
        memberService.changePassword(newPasswordDto);
        return Response.SUCCESS("비밀번호 변경 성공");
    }

    @PatchMapping()
    public Response<String> changeProfileNameAndMbti(@RequestBody Map<String, String> nameAndMbti) {
        memberService.changeProfileNameAndMbti(nameAndMbti.get("name"), nameAndMbti.get("mbti"));
        return Response.SUCCESS("프로필 이름과 mbti 값 변경 성공");
    }
}
