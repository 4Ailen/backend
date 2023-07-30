package com.aliens.friendship.domain.chat.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ChattingJwtTokenUtil {

    @Value("${app.jwt.secret.chatting-access-token-secret-key}")
    private String secret;
    long tokenValidityInSeconds =  	172800;


    public String generateToken(Long memberId,List<Long> roomIds ) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInSeconds);

        Claims claims = Jwts.claims();
        claims.put("memberId",memberId);
        // 매칭된 인원에 수에 대해서 가변적이기 때문에 다음과 같이 숫자를 기반으로 추가
        for(int i = 0 ; i < roomIds.size(); i ++ ){
            claims.put(String.valueOf(i),roomIds.get(i));
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(validity)
                .signWith(getSigningKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
