package com.aliens.friendship.domain.article.exception;

import com.aliens.friendship.global.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ArticleExceptionHandler {

    /**
     * ArticleCreationNotAllowedException 핸들링
     * Custom Exception
     */
    @ExceptionHandler(ArticleCreationNotAllowedException.class)
    protected ResponseEntity<ErrorResponse> handlingArticleCreationNotAllowedException(
            ArticleCreationNotAllowedException e
    ) {
        log.error("[handling ArticleCreationNotAllowedException] {}", e.getExceptionCode().getMessage());
        return new ResponseEntity<>(
                ErrorResponse.of(e.getExceptionCode()),
                HttpStatus.valueOf(e.getExceptionCode().getHttpStatus().value())
        );
    }
}