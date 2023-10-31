package com.aliens.friendship.domain.article.exception;

import com.aliens.friendship.global.error.ExceptionCode;
import com.aliens.friendship.global.error.ResourceNotFoundException;

public class ArticleCreationNotAllowedException
        extends ResourceNotFoundException {

    private ExceptionCode exceptionCode;

    public ArticleCreationNotAllowedException() {
        super(ArticleExceptionCode.ARTICLE_CREATION_NOT_ALLOWED);
        this.exceptionCode = ArticleExceptionCode.ARTICLE_CREATION_NOT_ALLOWED;
    }

    @Override
    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}