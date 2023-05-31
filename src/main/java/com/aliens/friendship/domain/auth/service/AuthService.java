package com.aliens.friendship.domain.auth.service;

import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.auth.exception.MemberPasswordMisMatchException;
import com.aliens.friendship.domain.auth.exception.RefreshTokenNotFoundException;
import com.aliens.friendship.domain.auth.exception.TokenException;
import com.aliens.friendship.domain.auth.model.principal.UserPrincipal;
import com.aliens.friendship.domain.auth.repository.RefreshTokenRepository;
import com.aliens.friendship.domain.auth.token.AuthToken;
import com.aliens.friendship.domain.auth.token.AuthTokenProvider;
import com.aliens.friendship.domain.auth.token.RefreshToken;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.exception.MemberNotFoundException;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.*;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final AuthTokenProvider tokenProvider;

    private final static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    /**
     * 토큰 재발급 비즈니스
     */
    @Transactional
    public TokenDto reissueToken(String expiredAccessToken, String refreshToken) {
        AuthToken authTokenOfExpiredAccessToken = createAuthTokenOfAccessToken(expiredAccessToken);
        Claims expiredTokenClaims = authTokenOfExpiredAccessToken.getExpiredTokenClaims();
        String email = (String) expiredTokenClaims.get("email");
        Collection<? extends GrantedAuthority> roles = getMemberAuthority(expiredTokenClaims.get("roles", String.class));

        AuthToken authTokenOfRefreshToken = createAuthTokenOfRefreshToken(refreshToken);

        // Refresh Token 검증
        try {
            tokenValidate(authTokenOfRefreshToken);
        } catch (TokenException e) {
            throw new TokenException(INVALID_REFRESH_TOKEN);
        }

        RefreshToken storedRefreshToken = refreshTokenRepository.findByEmailAndValue(email, refreshToken)
                .orElseThrow(RefreshTokenNotFoundException::new);

        AuthToken newAccessToken = createAccessToken(email, roles);

        AuthToken newRefreshToken = createRefreshToken(email, roles);
        storedRefreshToken.changeTokenValue(newRefreshToken.getValue());

        return TokenDto.of(
                newAccessToken.getValue(),
                newRefreshToken.getValue()
        );
    }

    /**
     * 로그아웃 비즈니스
     */
    @Transactional
    public void logout(String accessToken) {
        String email = (String) createAuthTokenOfAccessToken(accessToken).getTokenClaims().get("email");

        refreshTokenRepository.deleteAllByEmail(email);
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
    @Transactional
    public AuthToken createRefreshToken(
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

    /**
     * Access Token Value -> AuthToken
     */
    public AuthToken createAuthTokenOfAccessToken(String accessToken) {
        return tokenProvider.createAuthTokenOfAccessToken(accessToken);
    }

    // Refresh Token Value -> AuthToken
    private AuthToken createAuthTokenOfRefreshToken(String refreshToken) {
        return tokenProvider.createAuthTokenOfRefreshToken(refreshToken);
    }

    // 토큰에서 파싱한 사용자 권한 리스트 문자열을 각각 분리해서 GrantedAuthority List 로 반환
    private Collection<? extends GrantedAuthority> getMemberAuthority(String memberRoles) {
        System.out.println(memberRoles);
        return Arrays.stream(memberRoles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Token 유효성 검증
     */
    public void tokenValidate(AuthToken token) {
        Claims tokenClaims = token.getTokenClaims();
        if (tokenClaims == null)
            throw new TokenException(INVALID_TOKEN);

        if (!refreshTokenRepository.existsByEmail((String) tokenClaims.get("email")))
            throw new TokenException(LOGGED_OUT_TOKEN);
    }

    /**
     * 인가된 사용자 인증 정보 조회
     */
    public Authentication getAuthentication(AuthToken accessToken) {
        // access token 검증
        tokenValidate(accessToken);

        // access token claims 조회
        Claims claims = accessToken.getTokenClaims();

        // claims 값을 기반으로 Member Entity 조회
        Member savedMember = memberRepository.findByEmail((String) claims.get("email"))
                .orElseThrow(MemberNotFoundException::new);

        UserPrincipal userPrincipal = UserPrincipal.from(savedMember);

        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                accessToken,
                userPrincipal.getAuthorities()
        );
    }
}