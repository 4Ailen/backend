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


    private Key generateTokenKey(String tokenKey) {
        return Keys.hmacShaKeyFor(tokenKey.getBytes(StandardCharsets.UTF_8));
    }


    private Date generateTokenExpiry(
            long tokenExpiry
    ) {
        return new Date(new Date().getTime() + tokenExpiry);
    }



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


    public AuthToken createAuthTokenOfAccessToken(String accessToken) {
        return AuthToken.of(accessToken, accessTokenSecretKey);
    }


    public AuthToken createAuthTokenOfRefreshToken(String refreshToken) {
        return AuthToken.of(refreshToken, refreshTokenSecretKey);
    }
}