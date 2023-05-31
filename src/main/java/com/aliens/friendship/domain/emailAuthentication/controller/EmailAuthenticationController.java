package com.aliens.friendship.domain.emailAuthentication.controller;

import com.aliens.friendship.domain.emailAuthentication.service.EmailAuthenticationService;
import com.aliens.friendship.global.response.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailAuthenticationController {

    private final EmailAuthenticationService emailAuthenticationService;

    @PostMapping("/{email}/verification")
    public CommonResult sendEmail(@PathVariable String email) throws Exception {
        emailAuthenticationService.sendEmail(email);
        return CommonResult.of(
                OK.value(),
                "이메일 전송에 성공하였습니다."
        );
    }

    @GetMapping("/{email}/verification")
    public CommonResult verifyEmail(
            @PathVariable String email,
            @RequestParam("token") String token
    ) throws Exception {
        emailAuthenticationService.validateEmail(email, token);
        return CommonResult.of(
                OK.value(),
                "이메일 인증에 성공하였습니다."
        );
    }
}