package com.aliens.friendship.domain.emailauthentication.converter;

import com.aliens.db.emailauthentication.entity.EmailAuthenticationEntity;
import com.aliens.friendship.global.common.annotation.Converter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RequiredArgsConstructor
@Converter
public class EmailAuthenticationConverter {

    public  EmailAuthenticationEntity toAuthenticationEntity(String email) {
        EmailAuthenticationEntity emailAuthenticationEntity = EmailAuthenticationEntity.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .createdAt(Instant.now())
                .expirationAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .build();
        return emailAuthenticationEntity;
    }
}
