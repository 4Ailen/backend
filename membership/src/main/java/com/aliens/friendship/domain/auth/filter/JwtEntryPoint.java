package com.aliens.friendship.domain.auth.filter;

import com.aliens.friendship.global.error.ErrorResponse;
import com.aliens.friendship.global.error.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.AUTHENTICATION_ERROR;
import static com.aliens.friendship.domain.auth.exception.AuthExceptionCode.UNTRUSTED_CREDENTIAL;

@Slf4j
public class JwtEntryPoint
        implements AuthenticationEntryPoint {

    private static final String EXCEPTION_ATTRIBUTE_NAME = "exceptionCode";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        ExceptionCode exceptionCode = (ExceptionCode) request.getAttribute(EXCEPTION_ATTRIBUTE_NAME);

        if (exceptionCode != null) {
            setResponse(response, exceptionCode);
        } else if (authException.getClass() == InsufficientAuthenticationException.class) {
            authException.printStackTrace();
            setResponse(response, UNTRUSTED_CREDENTIAL);
        } else {
            // Error Message 만 넘어온 경우 범용 인증 예외 코드를 설정
            log.error("Responding with unauthorized error. Message := {}", authException.getMessage());
            setResponse(response, AUTHENTICATION_ERROR);
        }
    }

    // 에러 응답 생성 메소드
    private void setResponse(
            HttpServletResponse response,
            ExceptionCode exceptionCode
    ) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 한글 출력을 위해 getWriter() 사용
        response.getWriter().print(ErrorResponse.of(exceptionCode).convertJson());
    }
}