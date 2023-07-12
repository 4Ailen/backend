package com.aliens.friendship.domain.emailAuthentication.controller;

import com.aliens.friendship.domain.emailAuthentication.service.EmailAuthenticationService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailAuthenticationController {

    private final EmailAuthenticationService emailAuthenticationService;
    private final ResponseService responseService;

    @PostMapping("/{email}/verification")
    public CommonResult sendEmail(@PathVariable String email) throws Exception {
        emailAuthenticationService.sendEmail(email);
        return responseService.getSuccessResult(
                OK.value(),
                "이메일 전송에 성공하였습니다."
        );
    }

    @GetMapping("/{email}/verification")
    public ModelAndView verifyEmail(
            @PathVariable String email,
            @RequestParam("token") String token
    ) throws Exception {
        emailAuthenticationService.validateEmail(email, token);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("emailVerificationComplete");

        return modelAndView;
    }
}