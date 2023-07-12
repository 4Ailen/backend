package com.aliens.friendship.domain.member.controller;

import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.response.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class APIController {

    private final MemberService memberService;

    @GetMapping("/health")
    public ResponseEntity<CommonResult> health() {

        return ResponseEntity.ok(
                CommonResult.of(
                        "OK"
                )
        );
    }

    @PostMapping("/join")
    public ResponseEntity<CommonResult> join(@RequestBody JoinDto joinDto) throws Exception {
        memberService.join(joinDto);

        return ResponseEntity.ok(
                CommonResult.of(
                        "회원가입 완료"
                )
        );
    }
}