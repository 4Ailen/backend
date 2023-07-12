package com.aliens.friendship.domain.emailAuthentication.exception;

import com.aliens.friendship.global.error.ErrorResponse;
import com.aliens.friendship.global.error.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestControllerAdvice
public class EmailAuthenticationExceptionHandler {

    /**
     * EmailAlreadyRegisteredException handling (Custom Exception)
     */
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    protected ResponseEntity<ErrorResponse> handleEmailAlreadyRegisteredException(
            EmailAlreadyRegisteredException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(exceptionCode, exceptionCode.getMessage()),
                HttpStatus.valueOf(exceptionCode.getHttpStatus().value())
        );
    }

    /**
     * EmailVerificationTimeOutException handling (Custom Exception)
     */
    @ExceptionHandler(EmailVerificationTimeOutException.class)
    protected ModelAndView handleEmailVerificationTimeOutException(
            EmailVerificationTimeOutException e
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("emailVerificationTimeOut");
        log.error("{}", e.getMessage());

        return modelAndView;
    }

    /**
     * EmailInvalidTokenException handling (Custom Exception)
     */
    @ExceptionHandler(EmailInvalidTokenException.class)
    protected ModelAndView handleEmailInvalidTokenException(
            EmailInvalidTokenException e
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("emailVerificationInvalidToken");
        log.error("{}", e.getMessage());

        return modelAndView;
    }
}