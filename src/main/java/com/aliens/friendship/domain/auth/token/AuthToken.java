package com.aliens.friendship.domain.auth.token;

import com.aliens.friendship.domain.auth.exception.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.*;

public class AuthToken {
    private final Key key;

    @Getter
    private final String value;

    private AuthToken(
            String memberPersonalId,
            Collection<? extends GrantedAuthority> memberRoles,
            Date tokenExpiry,
            Key key
    ) {
        this.key = key;
        this.value = generateTokenValue(
                memberPersonalId,
                memberRoles,
                tokenExpiry
        );
    }

    private AuthToken(
            String tokenValue,
            Key key
    ) {
        this.value = tokenValue;
        this.key = key;
    }

    public static AuthToken of(
            String memberPersonalId,
            Collection<? extends GrantedAuthority> memberRoles,
            Date tokenExpiry,
            Key key
    ) {
        return new AuthToken(
                memberPersonalId,
                memberRoles,
                tokenExpiry,
                key
        );
    }

    public static AuthToken of(
            String tokenValue,
            Key key
    ) {
        return new AuthToken(
                tokenValue,
                key
        );
    }

    /**
     * Token Value 생성
     */
    private String generateTokenValue(
            String email,
            Collection<? extends GrantedAuthority> memberRoles,
            Date tokenExpiry
    ) {
        String roles = memberRoles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Claims claims = Jwts.claims()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(tokenExpiry);
        claims.put("email", email);
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Token Claim 조회
     */
    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(value)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(EXPIRED_TOKEN, e.getClaims());
        } catch (Exception e) {
            throw new TokenException(INVALID_TOKEN);
        }
    }

    /**
     * 만료된 Token Claim 조회
     */
    public Claims getExpiredTokenClaims() {
        try {
            getTokenClaims();
        } catch (TokenException e) {
            if (e.getExceptionCode() == EXPIRED_TOKEN) {
                return e.getExpiredTokenClaims();
            }

            throw e;
        }

        throw new TokenException(NOT_EXPIRED_TOKEN);
    }
}