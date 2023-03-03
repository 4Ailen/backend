package com.aliens.friendship.member.controller;

import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.controller.dto.MemberInfoDto;
import com.aliens.friendship.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
