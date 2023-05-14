package com.aliens.friendship.domain.member.controller;

import com.aliens.friendship.domain.jwt.domain.dto.LoginDto;
import com.aliens.friendship.domain.jwt.domain.dto.TokenDto;
import com.aliens.friendship.domain.jwt.util.JwtTokenUtil;
import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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


    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) throws Exception {
        return ResponseEntity.ok(memberService.login(loginDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        return ResponseEntity.ok(memberService.reissue(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken,
                       @RequestHeader("RefreshToken") String refreshToken) {
        String email = jwtTokenUtil.getEmail(memberService.resolveToken(accessToken));
        memberService.logout(TokenDto.of(accessToken, refreshToken), email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}