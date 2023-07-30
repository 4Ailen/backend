package com.aliens.friendship.domain.auth.converter;

import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.global.common.annotation.Converter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Converter
public class AuthConverter {
    public TokenDto toTokenDto(String accessToken,String refreshToken){
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return tokenDto;
    }
}
