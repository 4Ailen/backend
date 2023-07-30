package com.aliens.friendship.auth.business;


import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.auth.business.AuthBusiness;
import com.aliens.friendship.domain.auth.converter.AuthConverter;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.auth.service.AuthService;
import com.aliens.friendship.domain.auth.token.AuthToken;
import com.aliens.friendship.domain.auth.token.AuthTokenProvider;
import com.aliens.friendship.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MockAuthBusinessTest {

    @Mock
    private AuthTokenProvider tokenProvider;
    @Mock
    private AuthService authService;

    @Mock
    private MemberService memberService;

    @Mock
    private AuthConverter authConverter;

    @InjectMocks
    private AuthBusiness authBusiness;

    private MemberEntity memberEntity;
    private AuthToken accessToken;
    private AuthToken refreshToken;

    @BeforeEach
    public void setup() {
        memberEntity = MemberEntity.builder().email("test@test.com").status(MemberEntity.Status.APPLIED).build();
        accessToken = AuthToken.of("access_token", mock(Key.class));
        refreshToken = AuthToken.of("refresh_token", mock(Key.class));
    }

    @Test
    @DisplayName("로그인 - 성공")
    public void testLogin() throws Exception {
        // given
        LoginRequest request = LoginRequest.of("test@example.com", "password");
        Collection<? extends GrantedAuthority> roles = Collections.singletonList(() -> "ROLE_USER");
        TokenDto tokenDto = TokenDto.of("access_token", "refresh_token");

        // when
        when(memberService.findByEmail(request.getEmail())).thenReturn(memberEntity);
        when(authService.createAccessToken(request.getEmail(), memberEntity.getAuthorities())).thenReturn(accessToken);
        when(authService.createRefreshToken(request.getEmail(), memberEntity.getAuthorities())).thenReturn(refreshToken);
        when(authConverter.toTokenDto("access_token", "refresh_token")).thenReturn(tokenDto);

        // then
        TokenDto result = authBusiness.login(request);
        assertEquals(tokenDto, result);
    }
    // TODO: 토큰 재발급과 로그아웃에 대한 비즈니스 로직 MOCK 테스트코드 작성

}