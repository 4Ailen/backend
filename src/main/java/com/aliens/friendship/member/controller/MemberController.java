package com.aliens.friendship.member.controller;

import com.aliens.friendship.global.common.Response;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.controller.dto.MemberInfoDto;
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

    @GetMapping("/{email}")
    public Response<MemberInfoDto> getMember(@PathVariable String email) {
        return Response.SUCCESS(memberService.getMemberInfo(email));
    }




}
