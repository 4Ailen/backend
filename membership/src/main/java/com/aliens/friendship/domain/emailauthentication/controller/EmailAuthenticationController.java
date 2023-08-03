package com.aliens.friendship.domain.emailauthentication.controller;

import com.aliens.friendship.domain.emailauthentication.business.EmailAuthenticationBusiness;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailAuthenticationController {

    private final EmailAuthenticationBusiness emailAuthenticationBusiness;

    @GetMapping("/{email}/authentication-status")
    public ResponseEntity<SingleResult<Map<String, String>>> getMemberAuthenticationStatus(@PathVariable String email) {
        Map<String, String> status = new HashMap<>();

        String memberStatus = emailAuthenticationBusiness.getEmailAuthenticationStatus(email);
        status.put("status", memberStatus);

        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 조회하였습니다.",
                        status
                )
        );
    }

    @PostMapping("/{email}/verification")
    public ResponseEntity<CommonResult> sendEmail(@PathVariable String email) throws Exception {
        emailAuthenticationBusiness.sendAuthenticationEmail(email);

        return ResponseEntity.ok(
                CommonResult.of(
                        "이메일 전송에 성공하였습니다."
                )
        );
    }

    @GetMapping("/{email}/verification")
    public ModelAndView verifyEmail(
            @PathVariable String email,
            @RequestParam("token") String token
    ) throws Exception {
        emailAuthenticationBusiness.validateEmail(email,token);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("emailVerificationComplete");
        return modelAndView;
    }

}