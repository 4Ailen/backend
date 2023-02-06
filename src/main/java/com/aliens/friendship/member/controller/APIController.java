package com.aliens.friendship.member.controller;

import com.aliens.friendship.jwt.domain.dto.JoinDto;
import com.aliens.friendship.jwt.domain.dto.LoginDto;
import com.aliens.friendship.jwt.domain.dto.MemberInfo;
import com.aliens.friendship.jwt.domain.dto.TokenDto;
import com.aliens.friendship.member.service.MemberService;
import com.aliens.friendship.jwt.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class APIController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;


    @PostMapping("/join")
    public ResponseEntity<JoinDto> join(@RequestBody JoinDto joinDto) {
        return ResponseEntity.ok(memberService.join(joinDto));
    }


    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(memberService.login(loginDto));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken,
                       @RequestHeader("RefreshToken") String refreshToken) {
        String username = jwtTokenUtil.getUsername(memberService.resolveToken(accessToken));
        memberService.logout(TokenDto.of(accessToken, refreshToken), username);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/members/{email}")
    public MemberInfo getMemberInfo(@PathVariable String email) {
        return memberService.getMemberInfo(email);
    }

}
