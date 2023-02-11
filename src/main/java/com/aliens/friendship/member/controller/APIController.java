package com.aliens.friendship.member.controller;

import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.jwt.domain.dto.LoginDto;
import com.aliens.friendship.jwt.domain.dto.MemberInfo;
import com.aliens.friendship.jwt.domain.dto.TokenDto;
import com.aliens.friendship.member.service.MemberService;
import com.aliens.friendship.jwt.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class APIController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping("/join")
    public String join(@RequestBody JoinDto joinDto) throws Exception {
        memberService.join(joinDto);
        return "회원가입 완료";
    }

    @PostMapping("/join/admin")
    public String joinAdmin(@RequestBody JoinDto joinDto) {
        memberService.joinAdmin(joinDto);
        return "어드민 회원 가입 완료";
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(memberService.login(loginDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        return ResponseEntity.ok(memberService.reissue(refreshToken));
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String accessToken,
                       @RequestHeader("RefreshToken") String refreshToken) {
        String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
        memberService.logout(TokenDto.of(accessToken, refreshToken), username);
    }

    private String resolveToken(String accessToken) {
        return accessToken.substring(7);
    }
}
