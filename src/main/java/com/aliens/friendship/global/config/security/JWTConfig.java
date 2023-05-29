package com.aliens.friendship.global.config.security;

import com.aliens.friendship.domain.auth.token.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JWTConfig {

    @Value("${app.jwt.secret.access-token-secret-key}")
    private String accessTokenSecretKey;
    @Value("${app.jwt.expiry.access-token-expiry}")
    private long accessTokenExpiry;
    @Value("${app.jwt.secret.refresh-token-secret-key}")
    private String refreshTokenSecretKey;
    @Value("${app.jwt.expiry.refresh-token-expiry}")
    private long refreshTokenExpiry;

    /**
     * Token Provider
     */
    @Bean
    public AuthTokenProvider authTokenProvider() {
        return new AuthTokenProvider(
                accessTokenSecretKey,
                accessTokenExpiry,
                refreshTokenSecretKey,
                refreshTokenExpiry
        );
    }
}