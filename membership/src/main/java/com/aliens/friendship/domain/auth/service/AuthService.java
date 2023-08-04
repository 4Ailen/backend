package com.aliens.friendship.domain.auth.service;

import com.aliens.db.auth.entity.FcmTokenEntity;
import com.aliens.db.auth.entity.RefreshTokenEntity;
import com.aliens.db.auth.repository.FcmTokenRepository;
import com.aliens.db.auth.repository.RefreshTokenRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.auth.exception.*;
import com.aliens.friendship.domain.auth.model.UserPrincipal;
import com.aliens.friendship.domain.auth.token.AuthToken;
import com.aliens.friendship.domain.auth.token.AuthTokenProvider;
import com.aliens.friendship.domain.member.exception.MemberNotFoundException;
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

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.INVALID_TOKEN;
import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.LOGGED_OUT_TOKEN;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final AuthTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void deleteRefreshTokenByEmail(String email) {
        refreshTokenRepository.deleteAllByEmail(email);
    }

    @Transactional
    public void changeRefreshTokenEntity(String value, RefreshTokenEntity storedRefreshTokenEntity) {
        storedRefreshTokenEntity.changeTokenValue(value);
    }

    @Transactional
    public AuthToken createRefreshToken(String email, Collection<? extends GrantedAuthority> memberRoles) {
        AuthToken generateRefreshToken = tokenProvider.generateRefreshToken(email, memberRoles);

        refreshTokenRepository.save(
                RefreshTokenEntity.of(
                        email,
                        generateRefreshToken.getValue()
                )
        );

        return generateRefreshToken;
    }

    public void validateMemberStatus(MemberEntity.Status status) {
        if (status == MemberEntity.Status.WITHDRAWN) {
            throw new MemberNotFoundException();
        }
    }

    public void checkMemberPassword(String requestPassword, String savedPassword) {
        if (!passwordEncoder.matches(requestPassword, savedPassword))
            throw new MemberPasswordMisMatchException();
    }

    public AuthToken createAccessToken(String email, Collection<? extends GrantedAuthority> memberRoles) {
        return tokenProvider.generateAccessToken(email, memberRoles);
    }

    public AuthToken createAuthTokenOfAccessToken(String accessToken) {
        return tokenProvider.createAuthTokenOfAccessToken(accessToken);
    }

    public AuthToken createAuthTokenOfRefreshToken(String refreshToken) {
        return tokenProvider.createAuthTokenOfRefreshToken(refreshToken);
    }

    public void saveFcmToken(Long memberId, String fcmToken) {
        validateFcmToken(fcmToken);
        if (fcmTokenRepository.existsByValue(fcmToken)) {
            fcmTokenRepository.deleteAllByValue(fcmToken);
        }
        fcmTokenRepository.save(
                FcmTokenEntity.of(
                        memberId,
                        fcmToken
                )
        );
    }

    public void deleteFcmToken(String fcmToken) {
        validateFcmToken(fcmToken);
        fcmTokenRepository.deleteAllByValue(fcmToken);
    }

    public Collection<? extends GrantedAuthority> getMemberAuthority(String memberRoles) {
        System.out.println(memberRoles);
        return Arrays.stream(memberRoles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public boolean validateJwtToken(AuthToken token) {
        Claims tokenClaims = token.getTokenClaims();
        if (tokenClaims == null)
            throw new TokenException(INVALID_TOKEN);

        if (!refreshTokenRepository.existsByEmail((String) tokenClaims.get("email")))
            throw new TokenException(LOGGED_OUT_TOKEN);
        return true;
    }

    private void validateFcmToken(String fcmToken) {
        if (fcmToken == null) {
            throw new FcmTokenNotFoundException();
        } else if (fcmToken == "") {
            throw new InvalidFcmTokenException();
        }
    }

    public Authentication getAuthentication(AuthToken accessToken) {
        validateJwtToken(accessToken);

        Claims claims = accessToken.getTokenClaims();

        MemberEntity savedMemberEntity = memberRepository.findByEmail((String) claims.get("email"))
                .orElseThrow(MemberNotFoundException::new);

        UserPrincipal userPrincipal = UserPrincipal.from(savedMemberEntity);

        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                accessToken,
                userPrincipal.getAuthorities()
        );
    }

    public RefreshTokenEntity findRefreshTokenByEmilandValidRefreshToken(String email, String refreshToken) {

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByEmailAndValue(email, refreshToken)
                .orElseThrow(RefreshTokenNotFoundException::new);
        return refreshTokenEntity;
    }
}