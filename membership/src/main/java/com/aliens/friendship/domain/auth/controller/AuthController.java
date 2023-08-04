package com.aliens.friendship.domain.auth.controller;

import com.aliens.friendship.domain.auth.business.AuthBusiness;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.SingleResult;
import com.aliens.friendship.global.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthBusiness authBusiness;

    @PostMapping("/authentication")
    public ResponseEntity<SingleResult<TokenDto>> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest) throws Exception {
        String fcmToken = HeaderUtil.getFcmToken(httpServletRequest);
        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 토큰이 발급되었습니다.",
                        authBusiness.login(request, fcmToken)
                )
        );
    }

    @PostMapping("/reissue")
    public ResponseEntity<SingleResult<TokenDto>> reissue(
            HttpServletRequest request
    ) {
        String expiredAccessToken = HeaderUtil.getAccessToken(request);
        String refreshToken = HeaderUtil.getRefreshToken(request);

        return ResponseEntity.ok(
                SingleResult.of(
                        "성공적으로 토큰이 재발급되었습니다.",
                        authBusiness.reissueToken(expiredAccessToken,refreshToken)
                )
        );
    }

    @DeleteMapping("/logout")
    public ResponseEntity<CommonResult> logout(
            HttpServletRequest request
    ) {
        String accessToken = HeaderUtil.getAccessToken(request);
        authBusiness.logout(accessToken);

        return ResponseEntity.ok(
                CommonResult.of(
                        "성공적으로 로그아웃되었습니다."
                )
        );
    }
}