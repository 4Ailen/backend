package com.aliens.friendship.domain.auth.controller;

import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.auth.service.AuthService;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ResponseService;
import com.aliens.friendship.global.response.SingleResult;
import com.aliens.friendship.global.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final ResponseService responseService;

    @PostMapping("/authentication")
    public SingleResult<TokenDto> login(@RequestBody LoginRequest request) {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 토큰이 발급되었습니다.",
                authService.login(request)
        );
    }

    @PostMapping("/reissue")
    public SingleResult<TokenDto> reissue(
            HttpServletRequest request
    ) {
        String expiredAccessToken = HeaderUtil.getAccessToken(request);
        String refreshToken = HeaderUtil.getRefreshToken(request);

        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 토큰이 재발급되었습니다.",
                authService.reissueToken(expiredAccessToken, refreshToken)
        );
    }

    @DeleteMapping("/logout")
    public CommonResult logout(
            HttpServletRequest request
    ) {
        String accessToken = HeaderUtil.getAccessToken(request);
        authService.logout(accessToken);

        return responseService.getSuccessResult(
                OK.value(),
                "성공적으로 로그아웃되었습니다."
        );
    }
}