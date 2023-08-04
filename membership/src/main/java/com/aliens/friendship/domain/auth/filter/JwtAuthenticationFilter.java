package com.aliens.friendship.domain.auth.filter;

import com.aliens.friendship.domain.auth.exception.TokenException;
import com.aliens.friendship.domain.auth.service.AuthService;
import com.aliens.friendship.domain.auth.token.AuthToken;
import com.aliens.friendship.global.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.REQUEST_TOKEN_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final AuthService authService;

    private static final String EXCEPTION_ATTRIBUTE_NAME = "exceptionCode";
    private static final String TOKEN_REISSUE_REQUEST_URI = "/api/v1/auth/reissue";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Access Token 값 파싱
        String accessToken = HeaderUtil.getAccessToken(request);

        // 요청에 토큰이 존재하는지 검증
        if (accessToken == null) {
            request.setAttribute(EXCEPTION_ATTRIBUTE_NAME, REQUEST_TOKEN_NOT_FOUND);
            filterChain.doFilter(request, response);
            return;
        }

        AuthToken authAccessToken = authService.createAuthTokenOfAccessToken(accessToken);

        // Token 재발급 요청인 경우
        String path = request.getRequestURI();
        if (TOKEN_REISSUE_REQUEST_URI.equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authService.validateJwtToken(authAccessToken);
            SecurityContextHolder.getContext().setAuthentication(
                    authService.getAuthentication(authAccessToken)
            );
        } catch (TokenException e) {
            request.setAttribute(EXCEPTION_ATTRIBUTE_NAME, e.getExceptionCode());
        }

        filterChain.doFilter(request, response);
    }
}