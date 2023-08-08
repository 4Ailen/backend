package com.aliens.friendship.domain.auth.business;


import com.aliens.db.auth.entity.RefreshTokenEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.auth.converter.AuthConverter;
import com.aliens.friendship.domain.auth.dto.TokenDto;
import com.aliens.friendship.domain.auth.dto.request.LoginRequest;
import com.aliens.friendship.domain.auth.service.AuthService;
import com.aliens.friendship.domain.auth.token.AuthToken;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.common.annotation.Business;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@RequiredArgsConstructor
@Business
@Slf4j
public class AuthBusiness {
    private final AuthService authService;
    private final MemberService memberService;
    private final AuthConverter authConverter;

    /**
     * 로그인
     */
    public TokenDto login(LoginRequest request, String FcmToken) throws Exception {
        //회원 엔티티 조회
        MemberEntity memberEntity = memberService.findByEmail(request.getEmail());

        //회원 상태 검증
        authService.validateMemberStatus(memberEntity.getStatus());

        //회원 비밀번호 검증
        authService.checkMemberPassword(request.getPassword(), memberEntity.getPassword());

        // fcmToken 저장
        authService.saveFcmToken(memberEntity.getId(), FcmToken);

        //accessToken 발급
        String accessToken = authService.createAccessToken(request.getEmail(), memberEntity.getAuthorities()).getValue();

        //refreshToken 발급
        String refreshToken = authService.createRefreshToken(request.getEmail(), memberEntity.getAuthorities()).getValue();

        // accessToken + RefreshToken -> DTO
        TokenDto tokenDto = authConverter.toTokenDto(accessToken,refreshToken);

        return tokenDto;
    }

    /**
     * 토큰 재발급
     */
    public TokenDto reissueToken(String expiredAccessToken, String refreshToken) {
        // 만료된 accessToken 인증토큰 추출
        AuthToken authTokenOfExpiredAccessToken = authService.createAuthTokenOfAccessToken(expiredAccessToken);

        // 인증토큰에서 저장된 정보 추출
        Claims expiredTokenClaims = authTokenOfExpiredAccessToken.getExpiredTokenClaims();

        //이메일 추출
        String email = (String) expiredTokenClaims.get("email");

        //권한 추출
        Collection<? extends GrantedAuthority> roles = authService.getMemberAuthority(expiredTokenClaims.get("roles", String.class));

        // refreshToken 인증토큰 추출
        AuthToken authTokenOfRefreshToken = authService.createAuthTokenOfRefreshToken(refreshToken);

        // Refresh Token 유효 검증
        authService.validateJwtToken(authTokenOfRefreshToken);

        // 이메일과 리프레시토큰을 통해 리프레시토큰 엔티티 조회
        RefreshTokenEntity storedRefreshTokenEntity = authService.findRefreshTokenByEmilandValidRefreshToken(email,refreshToken);

        //새로운 토큰 두개 발급
        AuthToken newAccessToken = authService.createAccessToken(email, roles);
        AuthToken newRefreshToken = authService.createRefreshToken(email, roles);

        //리프레쉬 토큰 변경
        authService.changeRefreshTokenEntity(newRefreshToken.getValue(),storedRefreshTokenEntity);

        // accessToken + RefreshToken -> DTO
        TokenDto tokenDto = authConverter.toTokenDto(newAccessToken.getValue(),newRefreshToken.getValue());

        return  tokenDto;

    }

    /**
     * 로그아웃
     */
    public void logout(String accessToken, String fcmToken) {

        //이메일 추출
        String email = authService.createAuthTokenOfAccessToken(accessToken).getTokenClaims().get("email").toString();

        //이메일을 통한 RefreshToken 삭제 -> 이전 토큰으로 접근 불가
        authService.deleteRefreshTokenByEmail(email);

        authService.deleteFcmToken(fcmToken);
    }


}
