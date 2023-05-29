package com.aliens.friendship.domain.member.controller;

import com.aliens.friendship.domain.jwt.domain.dto.TokenDto;
import com.aliens.friendship.domain.jwt.util.JwtTokenUtil;
import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.response.CommonResult;
import com.aliens.friendship.global.response.ResponseService;
import com.aliens.friendship.global.response.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequiredArgsConstructor
public class APIController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;
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

    @PostMapping("/reissue")
    public SingleResult<TokenDto> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        return responseService.getSingleResult(
                OK.value(),
                "성공적으로 토큰이 재발급되었습니다.",
                memberService.reissue(refreshToken)
        );
    }

    @PostMapping("/logout")
    public CommonResult logout(
            @RequestHeader("Authorization") String accessToken,
            @RequestHeader("RefreshToken") String refreshToken
    ) {
        String email = jwtTokenUtil.getEmail(memberService.resolveToken(accessToken));
        memberService.logout(TokenDto.of(accessToken, refreshToken), email);
        return responseService.getSuccessResult(
                OK.value(),
                "성공적으로 로그아웃되었습니다."
        );
    }
}