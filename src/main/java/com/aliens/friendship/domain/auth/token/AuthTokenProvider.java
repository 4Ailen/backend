package com.aliens.friendship.domain.auth.token;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;

public class AuthTokenProvider {

    private final Key accessTokenSecretKey;
    private final long accessTokenExpiry;
    private final Key refreshTokenSecretKey;
    private final long refreshTokenExpiry;

    public AuthTokenProvider(
            String accessTokenSecretKeyString,
            long accessTokenExpiry,
            String refreshTokenSecretKeyString,
            long refreshTokenExpiry
    ) {
        this.accessTokenSecretKey = generateTokenKey(accessTokenSecretKeyString);
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenSecretKey = generateTokenKey(refreshTokenSecretKeyString);
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    // token key 문자열을 Key Object 로 변환
    private Key generateTokenKey(String tokenKey) {
        return Keys.hmacShaKeyFor(tokenKey.getBytes(StandardCharsets.UTF_8));
    }

    // token 만료 정수를 Date Object 로 변환
    private Date generateTokenExpiry(
            long tokenExpiry
    ) {
        return new Date(new Date().getTime() + tokenExpiry);
    }

    /**
     * Access Token 생성
     */
    public AuthToken generateAccessToken(
            String email,
            Collection<? extends GrantedAuthority> memberRoles
    ) {
        return AuthToken.of(
                email,
                memberRoles,
                generateTokenExpiry(accessTokenExpiry),
                accessTokenSecretKey
        );
    }

    /**
     * Refresh Token 생성
     */
    public AuthToken generateRefreshToken(
            String email,
            Collection<? extends GrantedAuthority> memberRoles
    ) {
        return AuthToken.of(
                email,
                memberRoles,
                generateTokenExpiry(refreshTokenExpiry),
                refreshTokenSecretKey
        );
    }

    /**
     * Access Token 값을 기반으로 AuthToken Object 생성
     */
    public AuthToken createAuthTokenOfAccessToken(String accessToken) {
        return AuthToken.of(accessToken, accessTokenSecretKey);
    }

    /**
     * Refresh Token 값을 기반으로 AuthToken 객체 생성
     */
    public AuthToken createAuthTokenOfRefreshToken(String refreshToken) {
        return AuthToken.of(refreshToken, refreshTokenSecretKey);
    }
}