package com.aliens.friendship.domain.auth.service;

import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.auth.exception.MemberPasswordMisMatchException;
import com.aliens.friendship.domain.auth.repository.RefreshTokenRepository;
import com.aliens.friendship.domain.auth.token.AuthToken;
import com.aliens.friendship.domain.auth.token.AuthTokenProvider;
import com.aliens.friendship.domain.auth.token.RefreshToken;
import com.aliens.friendship.domain.jwt.domain.dto.TokenDto;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.exception.MemberNotFoundException;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final AuthTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 비즈니스
     */
    public TokenDto login(LoginRequest request) {
        Member savedMember = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        validateMemberStatus(savedMember.getStatus());
        checkMemberPassword(savedMember.getPassword(), request.getPassword());

        return TokenDto.of(
                createAccessToken(request.getEmail(), savedMember.getAuthorities()).getValue(),
                createRefreshToken(request.getEmail(), savedMember.getAuthorities()).getValue()
        );
    }

    private void validateMemberStatus(Member.Status status) {
        if (status == Member.Status.WITHDRAWN) {
            throw new MemberNotFoundException();
        }
    }

    private void checkMemberPassword(String requestPassword, String savedPassword) {
        if (!passwordEncoder.matches(requestPassword, savedPassword))
            throw new MemberPasswordMisMatchException();
    }

    // Access Token 생성
    private AuthToken createAccessToken(
            String email,
            Collection<? extends GrantedAuthority> memberRoles
    ) {
        return tokenProvider.generateAccessToken(email, memberRoles);
    }

    // Refresh Token 생성
    private AuthToken createRefreshToken(
            String email,
            Collection<? extends GrantedAuthority> memberRoles
    ) {
        AuthToken generateRefreshToken = tokenProvider.generateRefreshToken(email, memberRoles);

        refreshTokenRepository.save(
                RefreshToken.of(
                        email,
                        generateRefreshToken.getValue()
                )
        );

        return generateRefreshToken;
    }
}