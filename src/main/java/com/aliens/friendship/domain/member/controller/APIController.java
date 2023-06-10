package com.aliens.friendship.domain.member.controller;

import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class APIController {

    private final MemberService memberService;
    private final ResponseService responseService;

    @GetMapping("/health")
    public CommonResult health() {

        return responseService.getSuccessResult(
                OK.value(),
                "OK"
        );
    }

    @PostMapping("/join")
    public CommonResult join(@RequestBody JoinDto joinDto) throws Exception {
        memberService.join(joinDto);

        return responseService.getSuccessResult(
                OK.value(),
                "회원가입 완료"
        );
    }
}