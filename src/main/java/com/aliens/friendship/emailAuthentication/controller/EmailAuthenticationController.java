package com.aliens.friendship.emailAuthentication.controller;

import com.aliens.friendship.emailAuthentication.service.EmailAuthenticationService;
import com.aliens.friendship.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailAuthenticationController {

    private final EmailAuthenticationService emailAuthenticationService;

    @PostMapping("/{email}/verification")
    public Response<String> sendEmail(@PathVariable String email) throws Exception {
        emailAuthenticationService.sendEmail(email);
        return Response.SUCCESS("이메일 전송 성공");
    }

    @GetMapping("/{email}/verification")
    public Response<String> verifyEmail(@PathVariable String email, @RequestParam("token") String token) throws Exception {
        emailAuthenticationService.validateEmail(email, token);
        return Response.SUCCESS("이메일 인증 성공");
    }
}
